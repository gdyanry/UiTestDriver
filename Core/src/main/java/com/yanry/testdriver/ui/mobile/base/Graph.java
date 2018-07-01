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
                        return p.getExpectation().isNeedCheck();
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
            roll(path, true);
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

    private boolean roll(Path path, boolean verifySuperPaths) {
        // 状态变化回调事件只能被动触发，即成为其他路径的父路径
        if (path.getEvent() instanceof StateChangeCallback) {
            return false;
        }
        // to avoid infinite loop
        if (!rollingPaths.add(path)) {
            return false;
        }
        return internalRoll(path, verifySuperPaths);
    }

    private boolean internalRoll(Path path, boolean verifySuperPaths) {
        // make sure environment states are satisfied.
        Optional<Map.Entry<Property, Object>> any = path.entrySet().stream().filter(state -> !state.getValue().
                equals(state.getKey().getCurrentValue(this))).findFirst();
        if (any.isPresent()) {
            Optional<Path> first = allPaths.stream().filter(p -> !failedPaths.contains(p))
                    .sorted(Comparator.comparingInt(p -> Integer.MAX_VALUE - p.getExpectation().getTotalMatchDegree(this, path) + p.getUnsatisfiedDegree(this))).findFirst();
            if (first.isPresent()) {
                int matchDegree = first.get().getExpectation().getTotalMatchDegree(this, path);
                if (matchDegree > 0) {
                    log("roll path to init state(%s, %s): %s", matchDegree, first.get().getUnsatisfiedDegree(this), Util.getPresentation(first.get()));
                    roll(first.get(), true);
                    return internalRoll(path, verifySuperPaths);
                }
            }
            Map.Entry<Property, Object> state = any.get();
            log("switch init state: %s, %s", Util.getPresentation(state.getKey()), state.getValue());
            if (state.getKey().switchTo(this, state.getValue(), true)) {
                return internalRoll(path, verifySuperPaths);
            } else {
                log("switch init state fail: %s, %s", Util.getPresentation(state.getKey()), state.getValue());
                return fail(path, state.getKey());
            }
        }
        // all environment states are satisfied by now.
        // trigger event
        Event inputEvent = path.getEvent();
        if (inputEvent instanceof StateEvent) {
            StateEvent event = (StateEvent) inputEvent;
            // roll path for this switch event.
            log("switch state event: %s", Util.getPresentation(event));
            if (!event.getProperty().switchTo(this, event.getFrom(), true) ||
                    // sibling paths of current path becomes super paths of the path where switch event takes place!
                    !event.getProperty().switchTo(this, event.getTo(), false)) {
                log("switch state event fail: %s", Util.getPresentation(event));
                return fail(path, event);
            }
        } else if (inputEvent instanceof ActionEvent) {
            ActionEvent event = (ActionEvent) inputEvent;
            event.processPreAction();
            // this is where the action event is performed!
            if (performAction(event)) {
                records.add(event);
            } else {
                log("perform action fail: %s", Util.getPresentation(event));
                return fail(path, event);
            }
        } else {
            log("unprocessed event: %s", Util.getPresentation(inputEvent));
            return fail(path, inputEvent);
        }
        // 兄弟路径指的是当前路径触发时顺带触发的其他路径；父路径是指由状态变迁形成的路径触发时本身形成状态变迁事件，由此导致触发的其他路径。
        List<Path> siblings = allPaths.stream().filter(p -> p.getEvent().equals(inputEvent) && p.getUnsatisfiedDegree(this) == 0).collect(Collectors.toList());
        // collect paths that share the same environment states and event
        final boolean[] result = new boolean[1];
        siblings.stream().sorted(Comparator.comparingInt(p -> allPaths.indexOf(p))).forEach(p -> {
            if (p == path) {
                log("verify path: %s", Util.getPresentation(p));
                result[0] = verify(p, verifySuperPaths);
            } else {
                log("verify sibling path: %s", Util.getPresentation(p));
                verify(p, true);
            }
        });
        return result[0];
    }

    private boolean fail(Path path, Object cause) {
        if (unprocessedPaths.remove(path)) {
            records.add(new MissedPath(path, cause));
        }
        failedPaths.add(path);
        rollingPaths.remove(path);
        return false;
    }

    private boolean verify(Path path, boolean verifySuperPaths) {
        Expectation expectation = path.getExpectation();
        Object fromValue = null;
        if (expectation instanceof PropertyExpectation) {
            Property property = ((PropertyExpectation) expectation).getProperty();
            fromValue = property.getCurrentValue(this);
        }
        boolean isPass = expectation.verify(this);
        if (expectation.isNeedCheck()) {
            records.add(new Assertion(expectation, isPass));
        }
        if (isPass) {
            if (expectation instanceof PropertyExpectation && verifySuperPaths) {
                PropertyExpectation propertyExpectation = (PropertyExpectation) expectation;
                verifySuperPaths(propertyExpectation.getProperty(), fromValue, propertyExpectation.getExpectedValue());
            }
        } else {
            failedPaths.add(path);
        }
        unprocessedPaths.remove(path);
        rollingPaths.remove(path);
        return isPass;
    }

    public <V> void verifySuperPaths(Property<V> property, V from, V to) {
        allPaths.stream().filter(p -> {
            if (!rollingPaths.contains(p)) {
                if (p.getEvent() instanceof StateEvent) {
                    StateEvent switchEvent = (StateEvent) p.getEvent();
                    if (switchEvent.getProperty().equals(property) && switchEvent.getTo().equals(to)
                            && (from == null || switchEvent.getFrom().equals(from)) && p.getUnsatisfiedDegree(this) == 0) {
                        return true;
                    }
                } else if (p.getEvent() instanceof StateChangeCallback) {
                    StateChangeCallback switchEvent = (StateChangeCallback) p.getEvent();
                    if (switchEvent.getProperty().equals(property) && switchEvent.getTo().test(to)
                            && (from == null || switchEvent.getFrom().test(from)) && p.getUnsatisfiedDegree(this) == 0) {
                        return true;
                    }
                }
            }
            return false;
        }).forEach(path -> {
            log("verify super path: %s", Util.getPresentation(path));
            verify(path, true);
        });
    }

    /**
     * try paths that satisfy the given predicates until one successfully rolled.
     *
     * @param endStatePredicate
     * @return
     */
    public <V> boolean findPathToRoll(BiPredicate<Property<V>, V> endStatePredicate, boolean verifySuperPaths) {
        return allPaths.stream().filter(p -> {
            if (!failedPaths.contains(p) && !rollingPaths.contains(p) && p.getExpectation() instanceof PropertyExpectation) {
                return isSatisfied(p.getExpectation(), endStatePredicate);
            }
            return false;
        }).sorted(Comparator.comparingInt(p -> {
            // TODO 细化排序值，寻找最短路径
            int unsatisfiedDegree = p.getUnsatisfiedDegree(this);
            log("compare value: %s - %s", unsatisfiedDegree, Util.getPresentation(p));
            return unsatisfiedDegree;
        })).filter(p -> {
            log("try to roll: %s", Util.getPresentation(p));
            return roll(p, verifySuperPaths);
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

    private void logCollection(String tag, Collection collection) {
        log(String.format("%s>>>>", tag));
        for (Object item : collection) {
            log(String.format("    %s", Util.getPresentation(item)));
        }
    }

    private void logStatus() {
        logCollection("rolling", rollingPaths);
        logCollection("failed", failedPaths);
        logCollection("record", records);
        log("unprocessed>>>>%s", unprocessedPaths.size());
    }

    @Override
    public <V> V checkState(StateToCheck<V> stateToCheck) {
        logStatus();
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
        logStatus();
        for (Communicator communicator : communicators) {
            if (communicator.performAction(actionEvent)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean verifyExpectation(Expectation expectation) {
        logStatus();
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
            ConsoleUtil.debug(1, s, objects);
        }
    }
}