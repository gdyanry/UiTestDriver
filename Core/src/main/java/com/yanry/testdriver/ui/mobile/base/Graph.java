/**
 *
 */
package com.yanry.testdriver.ui.mobile.base;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.event.ActionEvent;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.event.StateChangeCallback;
import com.yanry.testdriver.ui.mobile.base.event.StateEvent;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.expectation.PropertyExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.StaticPropertyExpectation;
import com.yanry.testdriver.ui.mobile.base.property.Property;
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
        processState = new ProcessState();
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
                        if (p.getEvent() instanceof StateEvent) {
                            StateEvent transitionEvent = (StateEvent) p.getEvent();
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
            log("traverse: %s", Util.getPresentation(path));
            roll(path);
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

    private boolean roll(Path path) {
        // to avoid infinite loop
        if (!rollingPaths.add(path)) {
            return false;
        }
        return internalRoll(path);
    }

    private boolean internalRoll(Path path) {
        // make sure environment states are satisfied.
        // TODO 按状态优先级排序
        Optional<Map.Entry<Property, Object>> any = path.entrySet().stream().filter(state -> !state.getValue().
                equals(state.getKey().getCurrentValue(this))).findFirst();
        if (any.isPresent()) {
            Map.Entry<Property, Object> state = any.get();
            log("switch init state: %s, %s", Util.getPresentation(state.getKey()), state.getValue());
            if (state.getKey().switchTo(this, state.getValue())) {
                return internalRoll(path);
            } else {
                log("switch init state fail: %s, %s", Util.getPresentation(state.getKey()), state.getValue());
                if (unprocessedPaths.remove(path)) {
                    records.add(new MissedPath(path, state.getKey()));
                }
                // TODO failedPaths.add
                return false;
            }
        }
        // all environment states are satisfied by now.
        // trigger event
        Event inputEvent = path.getEvent();
        // 兄弟路径指的是当前路径触发时顺带触发的其他路径；父路径是指由状态变迁形成的路径触发时本身形成状态变迁事件，由此导致触发的其他路径。
        List<Path> siblings = new LinkedList<>();
        if (inputEvent instanceof StateEvent) {
            StateEvent event = (StateEvent) inputEvent;
            // roll path for this switch event.
            log("switch state event: %s", Util.getPresentation(event));
            if (!event.getProperty().switchTo(this, event.getFrom()) ||
                    // sibling paths of current path becomes super paths of the path where switch
                    // event takes place!
                    !event.getProperty().switchTo(this, event.getTo())) {
                log("switch state event fail: %s", Util.getPresentation(event));
                if (unprocessedPaths.remove(path)) {
                    records.add(new MissedPath(path, event));
                }
                return false;
            }
        } else if (inputEvent instanceof ActionEvent) {
            ActionEvent event = (ActionEvent) inputEvent;
            event.processPreAction();
            // this is where the action event is performed!
            if (performAction(event)) {
                records.add(event);
                siblings.addAll(allPaths.stream().filter(p -> p.getEvent() == inputEvent && p
                        .isSatisfied(this)).collect(Collectors.toList()));
            } else {
                log("perform action fail: %s", Util.getPresentation(event));
                if (unprocessedPaths.remove(path)) {
                    records.add(new MissedPath(path, event));
                }
                return false;
            }
        } else if (inputEvent instanceof StateChangeCallback) {
            // 状态变化回调事件只能被动触发，即成为其他路径的父路径
            return false;
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
                log("verify path: %s", Util.getPresentation(p));
                result[0] = verify(p);
            } else {
                log("verify sibling path: %s", Util.getPresentation(p));
                verify(p);
            }
        });
        return result[0];
    }

    private boolean verify(Path path) {
        Expectation expectation = path.getExpectation();
        // 如果该期望（或者与其关联的期望）为属性期望，则其verify实现中必须要调用verifySupperPaths。
        boolean isPass = expectation.verify(this);
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

    public <V> boolean verifySuperPaths(Property<V> property, V from, V to, BooleanSupplier switchAction) {
        // this is where the switch action actually takes place!
        if (switchAction.getAsBoolean()) {
            allPaths.stream().filter(p -> {
                if (!rollingPaths.contains(p)) {
                    if (p.getEvent() instanceof StateEvent) {
                        StateEvent switchEvent = (StateEvent) p.getEvent();
                        if (switchEvent.getProperty() == property && switchEvent.getTo().equals(to)
                                && (from == null || switchEvent.getFrom().equals(from)) && p.isSatisfied(this)) {
                            return true;
                        }
                    } else if (p.getEvent() instanceof StateChangeCallback) {
                        StateChangeCallback switchEvent = (StateChangeCallback) p.getEvent();
                        if (switchEvent.getProperty() == property && switchEvent.getTo().test(to)
                                && (from == null || switchEvent.getFrom().test(from)) && p.isSatisfied(this)) {
                            return true;
                        }
                    }
                }
                return false;
            }).forEach(path -> {
                log("verify super path: %s", Util.getPresentation(path));
                verify(path);
            });
            return true;
        }
        return false;
    }

    /**
     * try paths that satisfy the given predicates until one successfully rolled.
     *
     * @param pathPredicate
     * @param endStatePredicate
     * @return
     */
    public <V> boolean findPathToRoll(Predicate<Path> pathPredicate, BiPredicate<Property<V>, V> endStatePredicate) {
        return allPaths.stream().filter(p -> {
            if (!failedPaths.contains(p) && !rollingPaths.contains(p) && p.getExpectation() instanceof PropertyExpectation) {
                return (pathPredicate == null || pathPredicate.test(p)) && isSatisfied(p.getExpectation(), endStatePredicate);
            }
            return false;
        }).sorted(Comparator.comparingInt(p -> {
            // TODO 细化排序值，寻找最短路径
            int i = p.isSatisfied(this) ? 0 : 1;
            log("compare value: %s - %s", i, Util.getPresentation(p));
            return i;
        })).filter(p -> {
            log("try to roll: %s", Util.getPresentation(p));
            return roll(p);
        }).findFirst().isPresent();
    }

    private <V> boolean isSatisfied(Expectation expectation, BiPredicate<Property<V>, V> endStatePredicate) {
        if (expectation instanceof StaticPropertyExpectation) {
            StaticPropertyExpectation<V> exp = (StaticPropertyExpectation<V>) expectation;
            if (exp.isSatisfied(endStatePredicate)) {
                return true;
            }
        }
        return expectation.getFollowingExpectations().stream().anyMatch(exp -> isSatisfied(exp, endStatePredicate));
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