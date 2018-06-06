/**
 *
 */
package com.yanry.testdriver.ui.mobile.base;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.event.*;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.process.ProcessState;
import com.yanry.testdriver.ui.mobile.base.property.Property;
import com.yanry.testdriver.ui.mobile.base.property.SwitchBySearchProperty;
import com.yanry.testdriver.ui.mobile.base.runtime.Assertion;
import com.yanry.testdriver.ui.mobile.base.runtime.Communicator;
import com.yanry.testdriver.ui.mobile.base.runtime.MissedPath;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import lib.common.interfaces.Loggable;
import lib.common.util.ConsoleUtil;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
public class Graph implements Communicator, Loggable {
    private boolean debug;
    private List<Path> allPaths;
    private Set<Path> unprocessedPaths;
    private Set<Path> failedPaths;
    private List<Object> records;
    private boolean isTraversing;
    private Set<Path> rollingPaths;
    private List<Communicator> communicators;
    private ProcessState processState;
    private List<Path> optionalPaths;

    public Graph(boolean debug) {
        this.debug = debug;
        allPaths = new LinkedList<>();
        records = new LinkedList<>();
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

    /**
     * @return 得到可供用户选择的测试用例（路径）列表。
     */
    public List<Path> prepare() {
        if (optionalPaths == null) {
            synchronized (this) {
                if (optionalPaths == null) {
                    optionalPaths = allPaths.parallelStream().filter(p -> {
                        // 如果路径的事件为属性变化，则该路径下的该属性的初始值定义是多余的，因为变化本身包含了属性的初值和终值
                        if (p.getEvent() instanceof ValueSwitchEvent) {
                            ValueSwitchEvent transitionEvent = (ValueSwitchEvent) p.getEvent();
                            Property property = transitionEvent.getProperty();
                            p.remove(property);
                        }
                        return p.getExpectation().ifRecord();
                    }).collect(Collectors.toList());
                }
            }
        }
        return optionalPaths;
    }

    /**
     * @param pathIndexes null indicates traversing all paths.
     * @return return null if traversing is currently processing.
     */
    public List<Object> traverse(int[] pathIndexes) {
        if (isTraversing) {
            return null;
        }
        isTraversing = true;
        if (optionalPaths == null) {
            prepare();
        }
        records.clear();
        failedPaths.clear();
        unprocessedPaths.clear();
        if (pathIndexes == null) {
            unprocessedPaths.addAll(optionalPaths);
        } else {
            for (int index : pathIndexes) {
                unprocessedPaths.add(optionalPaths.get(index));
            }
        }
        while (!unprocessedPaths.isEmpty() && isTraversing) {
            rollingPaths.clear();
            Path path = unprocessedPaths.stream().findAny().get();
            log("%n>>>>roll: %s%n", Util.getPresentation(path));
            roll(path, null);
        }
        List<Object> result = new ArrayList<>(records);
        isTraversing = false;
        return result;
    }

    /**
     * This should be called from a different thread.
     */
    public void abort() {
        isTraversing = false;
    }

    private boolean roll(Path path, List<Path> parentPaths) {
        // to avoid infinite loop
        if (!rollingPaths.add(path)) {
            return false;
        }
        return internalRoll(path, parentPaths);
    }

    private boolean internalRoll(Path path, List<Path> parentPaths) {
        // make sure environment states are satisfied.
        // TODO 按状态优先级排序
        Optional<Map.Entry<Property, Object>> any = path.entrySet().stream().filter(state -> !state.getValue().
                equals(state.getKey().getCurrentValue())).findFirst();
        if (any.isPresent()) {
            Map.Entry<Property, Object> state = any.get();
            if (state.getKey().switchTo(state.getValue(), null)) {
                return internalRoll(path, parentPaths);
            } else {
                if (unprocessedPaths.remove(path)) {
                    records.add(new MissedPath(path, state.getKey()));
                }
                // TODO failedPaths.add
                return false;
            }
        }
        // trigger event
        Event inputEvent = path.getEvent();
        List<Path> siblings = new LinkedList<>();
        if (inputEvent instanceof ValueSwitchEvent) {
            ValueSwitchEvent event = (ValueSwitchEvent) inputEvent;
            // roll path for this switch event.
            if (!event.getProperty().switchTo(event.getFrom(), null) ||
                    // sibling paths of current path becomes super paths of the path where switch
                    // event takes place!
                    !event.getProperty().switchTo(event.getTo(), siblings)) {
                if (unprocessedPaths.remove(path)) {
                    records.add(new MissedPath(path, event));
                }
                return false;
            }
        } else if (inputEvent instanceof DynamicSwitchEvent) {
            DynamicSwitchEvent event = (DynamicSwitchEvent) inputEvent;
            if (!switchTo(event.getProperty(), event.getFrom(), null) || !switchTo(event
                    .getProperty(), event.getTo(), siblings)) {
                if (unprocessedPaths.remove(path)) {
                    records.add(new MissedPath(path, event));
                }
                return false;
            }
        } else if (inputEvent instanceof ActionEvent) {
            ActionEvent event = (ActionEvent) inputEvent;
            event.processPreAction();
            if (performAction(event)) {
                records.add(event);
                siblings.addAll(allPaths.stream().filter(p -> p.getEvent() == inputEvent && p
                        .isSatisfied()).collect(Collectors.toList()));
            } else {
                if (unprocessedPaths.remove(path)) {
                    records.add(new MissedPath(path, event));
                }
                return false;
            }
        } else {
            if (unprocessedPaths.remove(path)) {
                records.add(new MissedPath(path, inputEvent));
            }
            return false;
        }
        // collect paths that share the same environment states and event
        final boolean[] result = new boolean[1];
        siblings.add(path);
        siblings.stream().distinct().sorted(Comparator.comparingInt(p -> allPaths.indexOf(p))).forEach(p -> {
            if (p == path) {
                log("%n>>>>selfVerify(%s): %s%n", parentPaths == null, Util.getPresentation(p));
                result[0] = verify(p, parentPaths);
            } else {
                log("%n>>>>selfVerify sibling: %s%n", Util.getPresentation(p));
                verify(p, null);
            }
        });
        return result[0];
    }

    private boolean verify(Path path, List<Path> parentPaths) {
        Expectation expectation = path.getExpectation();
        boolean isPass = expectation.verify(parentPaths);
        if (expectation.ifRecord()) {
            records.add(new Assertion(expectation, isPass));
        }
        if (!isPass) {
            failedPaths.add(path);
        }
        unprocessedPaths.remove(path);
        rollingPaths.remove(path);
        return isPass;
    }

    public <V> boolean verifySuperPaths(Property<V> property, V from, V to, List<Path> parentPaths,
                                        BooleanSupplier switchAction) {
        if (switchAction.getAsBoolean()) {
            List<Path> superPaths = allPaths.stream().filter(p -> {
                if (!rollingPaths.contains(p)) {
                    if (p.getEvent() instanceof ValueSwitchEvent) {
                        ValueSwitchEvent switchEvent = (ValueSwitchEvent) p.getEvent();
                        if (switchEvent.getProperty() == property && switchEvent.getTo() == to
                                && (from == null || switchEvent.getFrom().equals(from)) && p.isSatisfied()) {
                            return true;
                        }
                    } else if (p.getEvent() instanceof DynamicSwitchEvent) {
                        DynamicSwitchEvent switchEvent = (DynamicSwitchEvent) p.getEvent();
                        if (switchEvent.getProperty() == property && switchEvent.getTo().test(to) && (from == null ||
                                switchEvent.getFrom().test(from)) && p.isSatisfied()) {
                            return true;
                        }
                    } else if (p.getEvent() instanceof PassiveSwitchEvent) {
                        PassiveSwitchEvent switchEvent = (PassiveSwitchEvent) p.getEvent();
                        if (switchEvent.getProperty() == property && switchEvent.getTo().test(to) && (from == null ||
                                switchEvent.getFrom().test(from)) && p.isSatisfied()) {
                            return true;
                        }
                    }
                }
                return false;
            }).collect(Collectors.toList());
            if (parentPaths == null) {
                for (Path path : superPaths) {
                    log("%n>>>>selfVerify super: %s%n", Util.getPresentation(path));
                    verify(path, null);
                }
            } else {
                parentPaths.addAll(superPaths);
            }
            return true;
        }
        return false;
    }

    private <V> boolean switchTo(SwitchBySearchProperty<V> property, Predicate<V> to, List<Path>
            parentPaths) {
        if (to.test(property.getCurrentValue())) {
            return true;
        }
        return findPathToRoll(parentPaths, null, (p, v) -> p == property && to.test((V) v));
    }

    public <V> boolean switchToState(SwitchBySearchProperty<V> property, V to, List<Path> parentPaths) {
        return findPathToRoll(parentPaths, null, (prop, toVal) -> prop == property && to.equals(toVal));
    }

    /**
     * try paths that satisfy the given predicates until one successfully rolled.
     *
     * @param parentPaths
     * @param pathPredicate
     * @param endStatePredicate
     * @return
     */
    public boolean findPathToRoll(List<Path> parentPaths, Predicate<Path> pathPredicate,
                                  BiPredicate<SwitchBySearchProperty, Object> endStatePredicate) {
        return allPaths.stream().filter(p -> {
            if (!failedPaths.contains(p) && !rollingPaths.contains(p) && !(p.getEvent() instanceof
                    PassiveSwitchEvent)) {
                return (pathPredicate == null || pathPredicate.test(p)) && p.getExpectation().isSatisfied(endStatePredicate);
            }
            return false;
        }).sorted(Comparator.comparingInt(p -> {
            // TODO 细化排序值，寻找最短路径
            int i = p.isSatisfied() ? 0 : 1;
            log("%n>>>>compare %s: %s%n", i, Util.getPresentation(p));
            return i;
        })).filter(p -> {
            log("%n>>>>transit roll: %s%n", Util.getPresentation(p));
            return roll(p, parentPaths);
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
    public void log(String s, Object... objects) {
        if (debug) {
            ConsoleUtil.debug(s, objects);
        }
    }
}