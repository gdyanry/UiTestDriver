/**
 *
 */
package com.yanry.driver.core.model.base;

import com.yanry.driver.core.Utils;
import com.yanry.driver.core.model.base.Expectation.VerifyResult;
import com.yanry.driver.core.model.communicator.Communicator;
import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.event.Event;
import com.yanry.driver.core.model.event.StateChangeCallback;
import com.yanry.driver.core.model.event.StateEvent;
import com.yanry.driver.core.model.runtime.Assertion;
import com.yanry.driver.core.model.runtime.GraphWatcher;
import com.yanry.driver.core.model.runtime.MissedPath;
import com.yanry.driver.core.model.runtime.StateSwitch;
import com.yanry.driver.core.model.runtime.fetch.Obtainable;
import com.yanry.driver.core.model.state.ValuePredicate;
import lib.common.model.log.LogLevel;
import lib.common.model.log.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
public class Graph {
    private List<Path> allPaths;
    private Set<Path> unprocessedPaths;
    private Set<Path> failedPaths;
    private List<Object> records;
    private boolean isTraversing;
    private List<Communicator> communicators;
    private List<Path> optionalPaths;
    Map<CacheProperty, Object> cacheProperties;
    private GraphWatcher watcher;
    private long actionTimeFrame;
    private Set<Path> temp;
    private Set<Path> rollingPath;
    private AtomicInteger stackDepth;
    private Set<Expectation> pendingExpectations;
    private Set<Expectation> failedExpectation;

    public Graph(GraphWatcher watcher) {
        this.watcher = watcher;
        this.communicators = new LinkedList<>();
        allPaths = new LinkedList<>();
        records = new LinkedList<>();
        failedPaths = new HashSet<>();
        unprocessedPaths = new HashSet<>();
        cacheProperties = new HashMap<>();
        temp = new HashSet<>();
        rollingPath = new HashSet<>();
        stackDepth = new AtomicInteger();
        pendingExpectations = new HashSet<>();
        failedExpectation = new HashSet<>();
    }

    public void registerCommunicator(Communicator communicator) {
        communicators.add(communicator);
    }

    public void addPath(Path path) {
        allPaths.add(path);
    }

    void addPendingExpectation(Expectation expectation) {
        pendingExpectations.add(expectation);
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
        temp.clear();
        rollingPath.clear();
        pendingExpectations.clear();
        failedExpectation.clear();
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
            Logger.getDefault().d("traverse: %s", Utils.getPresentation(path));
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
     * @return 是否执行到verify()
     */
    private boolean deepRoll(Path path) {
        // 状态变化回调事件只能被动触发，即成为其他路径的父路径
        if (path.getEvent() instanceof StateChangeCallback) {
            return false;
        }
        notifyStandBy(path);
        while (shallowRoll(path)) {
            // 添加到temp里面说明已经执行到verify()了
            if (temp.contains(path)) {
                temp.clear();
                return true;
            }
            notifyStandBy(path);
        }
        // 还未执行到verify()便在shallowRoll()中返回false，说明没有可到达该path可执行环境且可用的路径
        temp.clear();
        unprocessedPaths.remove(path);
        failedPaths.add(path);
        return false;
    }

    private void notifyStandBy(Path path) {
        if (watcher != null) {
            watcher.onStandby(cacheProperties, unprocessedPaths, temp, failedPaths, path);
        }
    }

    /**
     * @param path
     * @return 是否触发ActionEvent
     */
    private boolean shallowRoll(Path path) {
        int depth = enterStack(String.format("roll: %s", Utils.getPresentation(path)));
        if (!rollingPath.add(path)) {
            exitStack(depth, true, "fail for repeating rolling");
            return false;
        }
        // make sure environment states are satisfied.
        Optional<Property> any = path.context.keySet().stream().filter(property -> !path.context.get(property).test(property.getCurrentValue())).findAny();
        if (any.isPresent()) {
            Property property = any.get();
            Object oldValue = property.getCurrentValue();
            ValuePredicate toState = path.context.get(property);
            String msg = String.format("switch init state: %s, %s", Utils.getPresentation(property), Utils.getPresentation(toState));
            Logger.getDefault().v(msg);
            if (!property.switchTo(toState)) {
                records.add(new MissedPath(path, new StateSwitch<>(property, oldValue, toState)));
                rollingPath.remove(path);
                exitStack(depth, true, msg);
                return false;
            }
            rollingPath.remove(path);
            exitStack(depth, false, msg);
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
            if (event.getFrom() != null && !event.getFrom().test(oldValue)) {
                String msg = String.format("switch state event(from): %s", Utils.getPresentation(event));
                Logger.getDefault().v(msg);
                if (!property.switchTo(event.getFrom())) {
                    records.add(new MissedPath(path, event));
                    rollingPath.remove(path);
                    exitStack(depth, true, msg);
                    return false;
                }
                rollingPath.remove(path);
                exitStack(depth, false, msg);
                return true;
            }
            String msg = String.format("switch state event(to): %s", Utils.getPresentation(event));
            Logger.getDefault().v(msg);
            if (!property.switchTo(event.getTo())) {
                records.add(new MissedPath(path, event));
                rollingPath.remove(path);
                exitStack(depth, true, msg);
                return false;
            }
            rollingPath.remove(path);
            exitStack(depth, false, msg);
            return true;
        } else if (inputEvent instanceof ActionEvent) {
            ActionEvent event = (ActionEvent) inputEvent;
            event.processPreAction();
            // this is where the action event is performed!
            if (!performAction(event)) {
                records.add(new MissedPath(path, event));
                rollingPath.remove(path);
                exitStack(depth, true, String.format("perform action fail: %s", Utils.getPresentation(event)));
                return false;
            }
            // collect paths that share the same environment states and event
            // 兄弟路径指的是当前路径触发时顺带触发的其他路径；父路径是指由状态变迁形成的路径触发时本身形成状态变迁事件，由此导致触发的其他路径。
            List<Path> pathToVerify = allPaths.stream().filter(p -> p.getEvent().equals(inputEvent) && p.getUnsatisfiedDegree(actionTimeFrame, false) == 0)
                    .sorted(Comparator.comparingInt(p -> allPaths.indexOf(p))).collect(Collectors.toList());
            pathToVerify.forEach(p -> p.getExpectation().preVerify());
            for (Path p : pathToVerify) {
                Logger.getDefault().v("verify path: %s", Utils.getPresentation(p));
                verify(p, false);
            }
            rollingPath.remove(path);
            exitStack(depth, false, "perform action success");
            return true;
        } else {
            records.add(new MissedPath(path, inputEvent));
            rollingPath.remove(path);
            exitStack(depth, true, String.format("unprocessed event: %s", Utils.getPresentation(inputEvent)));
            return false;
        }
    }

    private void verify(Path path, boolean isSuper) {
        String msg = String.format("%s: %s", isSuper ? "verify super" : "verify", Utils.getPresentation(path));
        int depth = enterStack(msg);
        Expectation expectation = path.getExpectation();
        VerifyResult result = expectation.verify(this);
        if (result != VerifyResult.Pending) {
            if (expectation.isNeedCheck()) {
                records.add(new Assertion(expectation, result == VerifyResult.Success));
            }
            if (result == VerifyResult.Failed) {
                failedExpectation.add(expectation);
                failedPaths.add(path);
            }
        }
        temp.add(path);
        unprocessedPaths.remove(path);
        exitStack(depth, result == VerifyResult.Failed, msg);
    }

    <V> void verifySuperPaths(Property<V> property, V from, V to) {
        // 处理pending expectation
        if (pendingExpectations.size() > 0) {
            ArrayList<Expectation> pending = new ArrayList<>(pendingExpectations);
            for (Expectation expectation : pending) {
                expectation.preVerify();
            }
            for (Expectation expectation : pending) {
                VerifyResult result = expectation.verify(this);
                if (result != VerifyResult.Pending) {
                    if (expectation.isNeedCheck()) {
                        records.add(new Assertion(expectation, result == VerifyResult.Success));
                    }
                    if (result == VerifyResult.Failed) {
                        failedExpectation.add(expectation);
                        allPaths.stream().filter(p -> !failedPaths.contains(p) && p.getExpectation() == expectation).forEach(p -> failedPaths.add(p));
                    }
                    pendingExpectations.remove(expectation);
                }
            }
        }
        if (!to.equals(from)) {
            List<Path> pathToVerify = allPaths.stream()
                    .filter(p -> !failedPaths.contains(p) && p.getEvent().matches(property, from, to) && p.getUnsatisfiedDegree(actionTimeFrame, false) == 0)
                    .collect(Collectors.toList());
            pathToVerify.forEach(path -> path.getExpectation().preVerify());
            pathToVerify.forEach(path -> verify(path, true));
        }
    }

    /**
     * try paths that satisfy the given predicates until an action event is triggered.
     *
     * @param expectationFilter
     * @return 是否触发ActionEvent
     */
    boolean findPathToRoll(Predicate<Expectation> expectationFilter) {
        String msg = "find path to roll";
        int depth = enterStack(msg);
        if (allPaths.stream().filter(p -> isSatisfied(p.getExpectation(), expectationFilter))
                .sorted(Comparator.comparingInt(p -> {
                    int unsatisfiedDegree = p.getUnsatisfiedDegree(actionTimeFrame, true);
                    Logger.getDefault().v("compare unsatisfied degree: %s - %s", unsatisfiedDegree, Utils.getPresentation(p));
                    return unsatisfiedDegree;
                })).filter(p -> {
                    Logger.getDefault().v("try to roll: %s", Utils.getPresentation(p));
                    return shallowRoll(p);
                }).findFirst().isPresent()) {
            exitStack(depth, false, msg);
            return true;
        }
        exitStack(depth, true, msg);
        return false;
    }

    private boolean isSatisfied(Expectation expectation, Predicate<Expectation> expectationFilter) {
        if (failedExpectation.contains(expectation)) {
            return false;
        }
        if (expectationFilter.test(expectation)) {
            return true;
        }
        return expectation.getFollowingExpectations().stream().anyMatch(exp -> isSatisfied(exp, expectationFilter));
    }

    <V> boolean findPathToRoll(BiPredicate<Property<V>, V> endStateFilter) {
        return findPathToRoll(e -> {
            if (e instanceof PropertyExpectation) {
                PropertyExpectation<V> exp = (PropertyExpectation<V>) e;
                if (endStateFilter.test(exp.getProperty(), exp.getExpectedValue())) {
                    return true;
                }
            }
            return false;
        });
    }

    boolean isValueFresh(Property property) {
        return actionTimeFrame > 0 && property.communicateTimeFrame == actionTimeFrame;
    }

    public <V> V obtainValue(Obtainable<V> obtainable) {
        Property<V> property = obtainable.getProperty();
        if (isValueFresh(property)) {
            return property.getCurrentValue();
        }
        for (Communicator communicator : communicators) {
            V value = communicator.checkState(obtainable);
            if (value != null) {
                property.communicateTimeFrame = actionTimeFrame;
                return value;
            }
        }
        throw new NullPointerException(String.format("unable to check state: %s", Utils.getPresentation(obtainable)));
    }

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

    public Boolean verifyExpectation(Expectation expectation) {
        for (Communicator communicator : communicators) {
            Boolean result = communicator.verifyExpectation(expectation);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private int enterStack(String msg) {
        int depth = stackDepth.incrementAndGet();
        Logger.getDefault().log(1, LogLevel.Verbose, "stack(%s) + %s", depth, msg);
        return depth;
    }

    private void exitStack(int depth, boolean isError, String msg) {
        if (isError) {
            Logger.getDefault().log(1, LogLevel.Error, "stack(%s) - %s", depth, msg);
        } else {
            Logger.getDefault().log(1, LogLevel.Verbose, "stack(%s) - %s", depth, msg);
        }
        stackDepth.decrementAndGet();
    }
}