/**
 *
 */
package com.yanry.testdriver.ui.mobile.base;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.event.ActionEvent;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.event.StateTransitionEvent;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.expectation.StatefulExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.process.ProcessState;
import com.yanry.testdriver.ui.mobile.base.process.StartProcess;
import com.yanry.testdriver.ui.mobile.base.process.StopProcess;
import com.yanry.testdriver.ui.mobile.base.runtime.Assertion;
import com.yanry.testdriver.ui.mobile.base.runtime.Communicator;
import com.yanry.testdriver.ui.mobile.base.runtime.MissedPath;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import lib.common.util.ConsoleUtil;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
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
        processState = new ProcessState() {
            @Override
            protected Graph getGraph() {
                return Graph.this;
            }
        };
        addPath(new Path(new StartProcess(), processState.getExpectation(Timing.IMMEDIATELY, true))
                .addInitState(processState, false));
        addPath(new Path(new StopProcess(), processState.getExpectation(Timing.IMMEDIATELY, false))
                .addInitState(processState, true));
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
        Optional<Map.Entry<StateProperty, Object>> any = path.entrySet().stream().filter(state -> !state.getValue().
                equals(state.getKey().getCurrentValue())).findAny();
        if (any.isPresent()) {
            Map.Entry<StateProperty, Object> state = any.get();
            if (state.getKey().transitTo(v -> state.getValue().equals(v), null)) {
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
        if (inputEvent instanceof StateTransitionEvent) {
            StateTransitionEvent event = (StateTransitionEvent) inputEvent;
            // roll path for this transition event.
            if (!event.getProperty().transitTo(event.getFrom(), null) ||
                    !event.getProperty().transitTo(v -> event.getTo().equals(v), siblingPathContainer)) {
                if (unprocessedPaths.remove(path)) {
                    testRecords.add(new MissedPath(path, event.getProperty()));
                }
                return false;
            }
        } else if (inputEvent instanceof ActionEvent) {
            ActionEvent event = (ActionEvent) inputEvent;
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

    public <V> boolean verifySuperPaths(StateProperty<V> property, V from, V to, List<Path> superPathContainer,
                                        Supplier<Boolean> doTransit) {
        List<Path> paths = allPaths.stream().filter(p -> {
            if (!rollingPaths.contains(p) && p.getEvent() instanceof StateTransitionEvent) {
                StateTransitionEvent transitionEvent = (StateTransitionEvent) p.getEvent();
                if (transitionEvent.getProperty() == property && transitionEvent.getTo() == to
                        && (from == null || transitionEvent.getFrom().test(from)) && p.isSatisfied()) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        if (doTransit.get()) {
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

    public <V> boolean transitToState(StateProperty<V> property, Predicate<V> to, List<Path> superPathContainer) {
        if (to.test(property.getCurrentValue())) {
            return true;
        }
        return findTransitionPathToRoll(superPathContainer, (p, pe) -> pe.getProperty().equals(property) && to.test(
                (V) pe.getValue()));
    }

    public boolean findTransitionPathToRoll(List<Path> superPathContainer, BiPredicate<Path, StatefulExpectation>
            judge) {
        return allPaths.stream().filter(p -> {
            if (failedPaths.contains(p) || !(p.getExpectation() instanceof StatefulExpectation)) {
                return false;
            }
            StatefulExpectation expectation = (StatefulExpectation) p.getExpectation();
            return judge.test(p, expectation);
        }).sorted(Comparator.comparingInt(p -> (p.isSatisfied() ? 0 : 1))).anyMatch(p -> {
            if (debug) {
                ConsoleUtil.debug("%n>>>>transit roll: %s%n", Util.getPresentation(p));
            }
            return roll(p, superPathContainer);
        });
    }

    @Override
    public <V> V checkState(StateToCheck<V> stateToCheck) {
        for (Communicator communicator : communicators) {
            V value = communicator.checkState(stateToCheck);
            if (value != null) {
                return value;
            }
        }
        return null;
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
}