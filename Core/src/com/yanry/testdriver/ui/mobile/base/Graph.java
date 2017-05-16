/**
 *
 */
package com.yanry.testdriver.ui.mobile.base;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.event.ActionEvent;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.event.StateSwitchEvent;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.process.ProcessState;
import com.yanry.testdriver.ui.mobile.base.property.QueryableProperty;
import com.yanry.testdriver.ui.mobile.base.property.SearchableSwitchableProperty;
import com.yanry.testdriver.ui.mobile.base.property.SwitchableProperty;
import com.yanry.testdriver.ui.mobile.base.runtime.Assertion;
import com.yanry.testdriver.ui.mobile.base.runtime.Communicator;
import com.yanry.testdriver.ui.mobile.base.runtime.MissedPath;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import lib.common.util.ConsoleUtil;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author yanry
 *         <p>
 *         Jan 5, 2017
 */
public class Graph implements Communicator {
    private boolean debug;
    private List<Path> allPaths;
    private Set<Path> unprocessedPaths;
    private Set<Path> failedPaths;
    private List<Object> testRecords;
    private boolean isTraversing;
    private Set<Path> rollingPaths;
    private List<Communicator> communicators;
    private ProcessState processState;
    private List<Path> optionalPaths;

    public Graph(boolean debug) {
        this.debug = debug;
        allPaths = new LinkedList<>();
        testRecords = new LinkedList<>();
        failedPaths = new HashSet<>();
        rollingPaths = new HashSet<>();
        unprocessedPaths = new HashSet<>();
        communicators = new ArrayList<>();
        processState = new ProcessState(this);
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public void addPath(Path path) {
        allPaths.add(path);
    }

    public void registerCommunicator(Communicator communicator) {
        if (!communicators.contains(communicator)) {
            communicators.add(communicator);
        }
    }

    public List<Path> prepare() {
        if (optionalPaths == null) {
            synchronized (this) {
                if (optionalPaths == null) {
                    optionalPaths = allPaths.stream().filter(p -> {
                        p.preProcess();
                        return p.getExpectation().ifRecord();
                    }).collect(Collectors.toList());
                }
            }
        }
        return optionalPaths;
    }

    /**
     * @param indexesOfOptionalPath null indicates traversing all paths.
     * @return return null if traversing is currently processing.
     */
    public List<Object> traverse(int[] indexesOfOptionalPath) {
        if (isTraversing) {
            return null;
        }
        isTraversing = true;
        if (optionalPaths == null) {
            prepare();
        }
        testRecords.clear();
        failedPaths.clear();
        unprocessedPaths.clear();
        if (indexesOfOptionalPath == null) {
            unprocessedPaths.addAll(optionalPaths);
        } else {
            for (int index : indexesOfOptionalPath) {
                unprocessedPaths.add(optionalPaths.get(index));
            }
        }
        while (!unprocessedPaths.isEmpty() && isTraversing) {
            rollingPaths.clear();
            Path path = unprocessedPaths.stream().findAny().get();
            if (debug) {
                ConsoleUtil.debug("%n>>>>roll: %s%n", Util.getPresentation(path));
            }
            roll(path, null);
        }
        List<Object> result = new ArrayList<>(testRecords);
        isTraversing = false;
        return result;
    }

    /**
     * This should be called from a different thread.
     */
    public void abort() {
        isTraversing = false;
    }

    private boolean roll(Path path, List<Path> superPathContainer) {
        // to avoid infinite loop
        if (!rollingPaths.add(path)) {
            return false;
        }
        return internalRoll(path, superPathContainer);
    }

    private boolean internalRoll(Path path, List<Path> superPathContainer) {
        // make sure environment states are satisfied.
        // TODO 按状态优先级排序
        Optional<Map.Entry<SwitchableProperty, Object>> any = path.entrySet().stream().filter(state -> !state.getValue().
                equals(state.getKey().getCurrentValue())).findFirst();
        if (any.isPresent()) {
            Map.Entry<SwitchableProperty, Object> state = any.get();
            if (state.getKey().switchTo(state.getValue(), null)) {
                return internalRoll(path, superPathContainer);
            } else {
                if (unprocessedPaths.remove(path)) {
                    testRecords.add(new MissedPath(path, state.getKey()));
                }
                return false;
            }
        }
        // trigger event
        Event inputEvent = path.getEvent();
        List<Path> siblingPathContainer = new LinkedList<>();
        if (inputEvent instanceof StateSwitchEvent) {
            StateSwitchEvent event = (StateSwitchEvent) inputEvent;
            // roll path for this switch event.
            if (!event.getProperty().switchTo(event.getFrom(), null) ||
                    // sibling path container of current path becomes super path container of the path where switch
                    // event takes place!
                    !event.getProperty().switchTo(event.getTo(), siblingPathContainer)) {
                if (unprocessedPaths.remove(path)) {
                    testRecords.add(new MissedPath(path, event.getProperty()));
                }
                return false;
            }
        } else if (inputEvent instanceof ActionEvent) {
            ActionEvent event = (ActionEvent) inputEvent;
            event.processPreAction();
            if (performAction(event)) {
                testRecords.add(event);
                siblingPathContainer.addAll(allPaths.stream().filter(p -> p.getEvent() == inputEvent && p
                        .isSatisfied()).collect(Collectors.toList()));
            } else {
                if (unprocessedPaths.remove(path)) {
                    testRecords.add(new MissedPath(path, event));
                }
                return false;
            }
        }
        // collect paths that share the same environment states and event
        final boolean[] result = new boolean[1];
        siblingPathContainer.add(path);
        siblingPathContainer.stream().distinct().sorted(Comparator.comparingInt(p -> allPaths.indexOf(p))).forEach(p -> {
            if (p == path) {
                if (debug) {
                    ConsoleUtil.debug("%n>>>>verify(%s): %s%n", superPathContainer == null, Util.getPresentation(p));
                }
                result[0] = verify(p, superPathContainer);
            } else {
                if (debug) {
                    ConsoleUtil.debug("%n>>>>verify sibling: %s%n", Util.getPresentation(p));
                }
                verify(p, null);
            }
        });
        return result[0];
    }

    private boolean verify(Path path, List<Path> superPathContainer) {
        Expectation expectation = path.getExpectation();
        boolean isPass = expectation.verify(superPathContainer);
        if (expectation.ifRecord()) {
            testRecords.add(new Assertion(expectation, isPass));
        }
        if (!isPass) {
            failedPaths.add(path);
        } else if (!path.getFollowingActions().isEmpty()) {
            for (Consumer<List<Path>> followingAction : path.getFollowingActions()) {
                followingAction.accept(superPathContainer);
            }
        }
        unprocessedPaths.remove(path);
        rollingPaths.remove(path);
        return isPass;
    }

    public <V> boolean verifySuperPaths(SwitchableProperty<V> property, V from, V to, List<Path> superPathContainer,
                                        Supplier<Boolean> doSwitch) {
        List<Path> paths = allPaths.stream().filter(p -> {
            if (!rollingPaths.contains(p) && p.getEvent() instanceof StateSwitchEvent) {
                StateSwitchEvent transitionEvent = (StateSwitchEvent) p.getEvent();
                if (transitionEvent.getProperty() == property && transitionEvent.getTo() == to
                        && (from == null || transitionEvent.getFrom().equals(from)) && p.isSatisfied()) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        if (doSwitch.get()) {
            if (superPathContainer == null) {
                for (Path path : paths) {
                    if (debug) {
                        ConsoleUtil.debug("%n>>>>verify super: %s%n", Util.getPresentation(path));
                    }
                    verify(path, null);
                }
            } else {
                superPathContainer.addAll(paths);
            }
            return true;
        }
        return false;
    }

    public <V> boolean switchToState(SearchableSwitchableProperty<V> property, V to, List<Path> superPathContainer, Supplier<Boolean>
            finalCheck) {
        return findPathToRoll(superPathContainer, (path, prop, toVal) -> prop.equals
                (property) && to.equals(toVal), finalCheck == null ? () -> to.equals(property.getCurrentValue()) : finalCheck);
    }

    public <V> boolean findPathToRoll(List<Path> superPathContainer, SwitchPredicate<V> predicate,
                                      Supplier<Boolean> finalCheck) {
        return allPaths.stream().filter(p -> {
            if (!failedPaths.contains(p) && !rollingPaths.contains(p)) {
                if (p.getExpectation() instanceof SearchableSwitchableProperty.SwitchablePropertyExpectation) {
                    SearchableSwitchableProperty<V>.SwitchablePropertyExpectation expectation =
                            (SearchableSwitchableProperty<V>.SwitchablePropertyExpectation) p.getExpectation();
                    if (predicate.test(p, expectation.getProperty(), expectation.getValue())) {
                        return true;
                    }
                }
                for (Consumer<List<Path>> followingAction : p.getFollowingActions()) {
                    if (followingAction instanceof SearchableSwitchableProperty.SwitchablePropertyExpectation) {
                        SearchableSwitchableProperty<V>.SwitchablePropertyExpectation expectation =
                                (SearchableSwitchableProperty<V>.SwitchablePropertyExpectation) followingAction;
                        if (predicate.test(p, expectation.getProperty(), expectation.getValue())) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }).sorted(Comparator.comparingInt(p -> {
            // TODO 细化排序值，寻找最短路径
            int i = p.isSatisfied() ? 0 : 1;
            if (debug) {
                ConsoleUtil.debug("%n>>>>compare %s: %s%n", i, Util.getPresentation(p));
            }
            return i;
        })).filter(p -> {
            if (debug) {
                ConsoleUtil.debug("%n>>>>transit roll: %s%n", Util.getPresentation(p));
            }
            return roll(p, superPathContainer) && finalCheck.get();
        }).findFirst().isPresent();
    }

    @Override
    public <V> V checkState(StateToCheck<V> stateToCheck) {
        for (Communicator communicator : communicators) {
            V value = communicator.checkState(stateToCheck);
            if (value != null) {
                return value;
            }
        }
        throw new NullPointerException(Util.getPresentation(stateToCheck).toString());
    }

    @Override
    public boolean performAction(ActionEvent actionEvent) {
        for (Communicator communicator : communicators) {
            if (communicator.performAction(actionEvent)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean verifyExpectation(Expectation expectation) {
        for (Communicator communicator : communicators) {
            Boolean result = communicator.verifyExpectation(expectation);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public String queryValue(QueryableProperty property) {
        for (Communicator communicator : communicators) {
            String value = communicator.queryValue(property);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}