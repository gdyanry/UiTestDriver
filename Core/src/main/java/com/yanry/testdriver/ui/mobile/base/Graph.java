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
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.property.Property;
import com.yanry.testdriver.ui.mobile.base.runtime.*;
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
    private List<Communicator> communicators;
    private List<Path> optionalPaths;
    private Map<CacheProperty, Object> cacheProperties;
    private GraphWatcher watcher;
    private long actionTimeFrame;
    private Set<Path> successTemp;

    public Graph(boolean debug) {
        this.debug = debug;
        allPaths = new LinkedList<>();
        records = new LinkedList<>();
        failedPaths = new HashSet<>();
        unprocessedPaths = new HashSet<>();
        communicators = new ArrayList<>();
        cacheProperties = new HashMap<>();
        successTemp = new HashSet<>();
    }

    public void addPath(Path path) {
        allPaths.add(path);
    }

    public <V> V getCacheValue(CacheProperty<V> property) {
        return (V) cacheProperties.get(property);
    }

    public <V> void setCacheValue(CacheProperty<V> property, V value) {
        cacheProperties.put(property, value);
    }

    public boolean isValueFresh(Property property) {
        return actionTimeFrame > 0 && property.getCommunicateTimeFrame() == actionTimeFrame;
    }

    public void setWatcher(GraphWatcher watcher) {
        this.watcher = watcher;
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
                    optionalPaths = allPaths.parallelStream().filter(p -> needCheck(p.getExpectation())).collect(Collectors.toList());
                }
            }
        }
        return optionalPaths;
    }

    private boolean needCheck(Expectation expectation) {
        return expectation.isNeedCheck() || expectation.getFollowingExpectations().parallelStream().anyMatch(exp -> needCheck(exp));
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
        } else if (pathIndexes.length >= optionalPaths.size()) {
            return Collections.EMPTY_LIST;
        } else {
            for (int index : pathIndexes) {
                unprocessedPaths.add(optionalPaths.get(index));
            }
        }
        while (!unprocessedPaths.isEmpty() && isTraversing) {
            Path path = unprocessedPaths.stream().sorted(Comparator.comparingInt(p -> p.getUnsatisfiedDegree(actionTimeFrame, true))).findFirst().get();
            debug("traverse: %s", Util.getPresentation(path));
            deepRoll(path);
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

    /**
     * @param path
     * @return 是否成功
     */
    private boolean deepRoll(Path path) {
        // 状态变化回调事件只能被动触发，即成为其他路径的父路径
        if (path.getEvent() instanceof StateChangeCallback) {
            return false;
        }
        notifyStandBy(path);
        while (shallowRoll(path)) {
            if (successTemp.contains(path)) {
                successTemp.clear();
                return true;
            } else if (failedPaths.contains(path)) {
                successTemp.clear();
                return false;
            }
            notifyStandBy(path);
        }
        // 未执行verify()
        unprocessedPaths.remove(path);
        failedPaths.add(path);
        return false;
    }

    private void notifyStandBy(Path path) {
        if (watcher != null) {
            watcher.onStandby(cacheProperties, unprocessedPaths, successTemp, failedPaths, path);
        }
    }

    /**
     * @param path
     * @return 是否触发ActionEvent
     */
    private boolean shallowRoll(Path path) {
        // make sure environment states are satisfied.
        Optional<Property> any = path.keySet().stream().filter(property -> !path.get(property).equals(property.getCurrentValue())).findAny();
        if (any.isPresent()) {
            Property property = any.get();
            Object oldValue = property.getCurrentValue();
            Object toValue = path.get(property);
            debug("switch init state: %s, %s", Util.getPresentation(property), Util.getPresentation(toValue));
            if (!property.switchTo(toValue)) {
                error("switch init state failed: %s, %s", Util.getPresentation(property), Util.getPresentation(toValue));
                records.add(new MissedPath(path, new StateEvent<>(property, oldValue, toValue)));
                return false;
            }
            return true;
        }
        // all environment states are satisfied by now.
        // trigger event
        Event inputEvent = path.getEvent();
        if (inputEvent instanceof StateEvent) {
            StateEvent event = (StateEvent) inputEvent;
            // roll path for this switch event.
            Property property = event.getProperty();
            Object oldValue = property.getCurrentValue();
            if (event.getFrom() != null && !event.getFrom().equals(oldValue)) {
                debug("switch from state event: %s", Util.getPresentation(event));
                if (!property.switchTo(event.getFrom())) {
                    error("switch from state event failed: %s", Util.getPresentation(event));
                    records.add(new MissedPath(path, event));
                    return false;
                }
                return true;
            }
            debug("switch to state event: %s", Util.getPresentation(event));
            if (!property.switchTo(event.getTo())) {
                error("switch to state event failed: %s", Util.getPresentation(event));
                records.add(new MissedPath(path, event));
                return false;
            }
            return true;
        } else if (inputEvent instanceof ActionEvent) {
            ActionEvent event = (ActionEvent) inputEvent;
            event.processPreAction();
            // this is where the action event is performed!
            if (!performAction(event)) {
                error("perform action fail: %s", Util.getPresentation(event));
                records.add(new MissedPath(path, event));
                return false;
            }
        } else {
            error("unprocessed event: %s", Util.getPresentation(inputEvent));
            records.add(new MissedPath(path, inputEvent));
            return false;
        }
        // collect paths that share the same environment states and event
        // 兄弟路径指的是当前路径触发时顺带触发的其他路径；父路径是指由状态变迁形成的路径触发时本身形成状态变迁事件，由此导致触发的其他路径。
        allPaths.stream().filter(p -> !failedPaths.contains(p) && p.getEvent().equals(inputEvent) && p.getUnsatisfiedDegree(actionTimeFrame, false) == 0)
                .sorted(Comparator.comparingInt(p -> allPaths.indexOf(p))).forEach(p -> {
            debug("verify path: %s", Util.getPresentation(p));
            verify(p);
        });
        return true;
    }

    private void verify(Path path) {
        Expectation expectation = path.getExpectation();
        expectation.preVerify();
        boolean isPass = expectation.verify();
        if (expectation.isNeedCheck()) {
            records.add(new Assertion(expectation, isPass));
        }
        if (isPass) {
            successTemp.add(path);
        } else {
            error("verify path failed: %s.", Util.getPresentation(path));
            failedPaths.add(path);
        }
        unprocessedPaths.remove(path);
    }

    public <V> void verifySuperPaths(Property<V> property, V from, V to) {
        if (!to.equals(from)) {
            allPaths.stream().filter(p -> !failedPaths.contains(p) && p.getEvent().matches(property, from, to) && p.getUnsatisfiedDegree(actionTimeFrame, false) == 0)
                    .forEach(path -> {
                        debug("verify super path: %s", Util.getPresentation(path));
                        verify(path);
                    });
        }
    }

    /**
     * try paths that satisfy the given predicates until an action event is triggered.
     *
     * @param endStatePredicate
     * @return
     */
    public <V> boolean findPathToRoll(BiPredicate<Property<V>, V> endStatePredicate) {
        if (!allPaths.stream().filter(p -> {
            if (!failedPaths.contains(p)) {
                return isSatisfied(p.getExpectation(), endStatePredicate);
            }
            return false;
        }).sorted(Comparator.comparingInt(p -> {
            int unsatisfiedDegree = p.getUnsatisfiedDegree(actionTimeFrame, true);
            debug("compare unsatisfied degree: %s - %s", unsatisfiedDegree, Util.getPresentation(p));
            return unsatisfiedDegree;
        })).filter(p -> {
            debug("try to roll: %s", Util.getPresentation(p));
            return shallowRoll(p);
        }).findFirst().isPresent()) {
            error("find path to roll failed.");
            return false;
        }
        return true;
    }

    private <V> boolean isSatisfied(Expectation expectation, BiPredicate<Property<V>, V> endStatePredicate) {
        if (expectation instanceof PropertyExpectation) {
            PropertyExpectation<V> exp = (PropertyExpectation<V>) expectation;
            if (exp.isSatisfied(endStatePredicate)) {
                return true;
            }
        }
        return expectation.getFollowingExpectations().stream().anyMatch(exp -> isSatisfied(exp, endStatePredicate));
    }

    @Override
    public <V> V checkState(StateToCheck<V> stateToCheck) {
        CacheProperty<V> property = stateToCheck.getProperty();
        if (isValueFresh(property)) {
            return property.getCurrentValue();
        }
        for (Communicator communicator : communicators) {
            V value = communicator.checkState(stateToCheck);
            if (value != null) {
                property.setCommunicateTimeFrame(actionTimeFrame);
                return value;
            }
        }
        throw new NullPointerException(String.format("unable to check state: %s", Util.getPresentation(stateToCheck)));
    }

    @Override
    public String fetchValue(Property<String> property) {
        if (isValueFresh(property)) {
            return property.getCurrentValue();
        }
        for (Communicator communicator : communicators) {
            String value = communicator.fetchValue(property);
            if (value != null) {
                property.setCommunicateTimeFrame(actionTimeFrame);
                return value;
            }
        }
        throw new NullPointerException(String.format("unable to fetch value: %s", Util.getPresentation(property)));
    }

    @Override
    public boolean performAction(ActionEvent actionEvent) {
        records.add(actionEvent);
        for (Communicator communicator : communicators) {
            if (communicator.performAction(actionEvent)) {
                actionTimeFrame = System.currentTimeMillis();
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
    public void debug(String s, Object... objects) {
        if (debug) {
            ConsoleUtil.debug(1, s, objects);
        }
    }

    @Override
    public void error(String s, Object... objects) {
        if (debug) {
            ConsoleUtil.error(1, s, objects);
        }
    }
}