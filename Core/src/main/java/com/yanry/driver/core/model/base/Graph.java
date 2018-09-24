/**
 *
 */
package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.base.Expectation.VerifyResult;
import com.yanry.driver.core.model.communicator.Communicator;
import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.event.TransitionEvent;
import com.yanry.driver.core.model.runtime.Assertion;
import com.yanry.driver.core.model.runtime.GraphWatcher;
import com.yanry.driver.core.model.runtime.fetch.Obtainable;
import lib.common.model.log.LogLevel;
import lib.common.model.log.Logger;
import lib.common.util.object.ObjectUtil;

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
    private static final String TYPE_SYMBOL = "#";
    private List<Path> allPaths;
    private Set<Path> unprocessedPaths;
    private Set<Path> failedPaths;
    private List<Object> records;
    private boolean isTraversing;
    private List<Communicator> communicators;
    private List<Path> optionalPaths;
    Map<Property, Object> propertyCache;
    private GraphWatcher watcher;
    private long actionTimeFrame;
    private Set<Path> verifiedPaths;
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
        propertyCache = new HashMap<>();
        verifiedPaths = new HashSet<>();
        rollingPath = new HashSet<>();
        stackDepth = new AtomicInteger();
        pendingExpectations = new HashSet<>();
        failedExpectation = new HashSet<>();
    }

    public static Object getPresentation(Object object) {
        return ObjectUtil.getPresentation(object, TYPE_SYMBOL);
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
        verifiedPaths.clear();
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
            Logger.getDefault().d("traverse: %s", getPresentation(path));
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

    public boolean postAction(ActionEvent actionEvent) {
        records.add(actionEvent);
        for (Communicator communicator : communicators) {
            if (communicator.performAction(actionEvent)) {
                actionTimeFrame = System.currentTimeMillis();
                List<Path> pathToVerify = allPaths.stream()
                        .filter(p -> p.getEvent().equals(actionEvent) && p.getUnsatisfiedDegree(actionTimeFrame, false) == 0)
                        .collect(Collectors.toList());
                pathToVerify.forEach(p -> p.getExpectation().preVerify());
                for (Path p : pathToVerify) {
                    Logger.getDefault().v("verify path: %s", getPresentation(p));
                    verify(p, false);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * @param path
     * @return 是否执行到verify()
     */
    private boolean deepRoll(Path path) {
        ActionEvent actionEvent;
        while ((actionEvent = roll(path)) != null) {
            if (postAction(actionEvent)) {
                if (verifiedPaths.contains(path)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void notifyStandBy(Path path) {
        if (watcher != null) {
            watcher.onStandby(propertyCache, unprocessedPaths, verifiedPaths, failedPaths, path);
        }
    }

    private ActionEvent roll(Path path) {
        // make sure environment states are satisfied.
        for (Property property : path.context.keySet()) {
            ValuePredicate valuePredicate = path.context.get(property);
            if (!valuePredicate.test(property.getCurrentValue())) {
                return property.switchTo(valuePredicate);
            }
        }
        // all environment states are satisfied by now.
        // trigger event
        Event inputEvent = path.getEvent();
        if (inputEvent instanceof TransitionEvent) {
            TransitionEvent event = (TransitionEvent) inputEvent;
            // roll path for this switch event.
            Property property = event.getProperty();
            Object oldValue = property.getCurrentValue();
            if (event.getFrom() != null && !event.getFrom().test(oldValue)) {
                String msg = String.format("switch state event(from): %s", getPresentation(event));
                Logger.getDefault().v(msg);
                return property.switchTo(event.getFrom());
            }
            String msg = String.format("switch state event(to): %s", getPresentation(event));
            Logger.getDefault().v(msg);
            return property.switchTo(event.getTo());
        } else if (inputEvent instanceof ActionEvent) {
            ActionEvent event = (ActionEvent) inputEvent;
            return event;
        }
        return null;
    }

    private void verify(Path path, boolean isSuper) {
        String msg = String.format("%s: %s", isSuper ? "verify super" : "verify", getPresentation(path));
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
        verifiedPaths.add(path);
        unprocessedPaths.remove(path);
        exitStack(depth, result == VerifyResult.Failed, msg);
    }

    // 兄弟路径指的是当前路径触发时顺带触发的其他路径；父路径是指由状态变迁形成的路径触发时本身形成状态变迁事件，由此导致触发的其他路径。
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

    ActionEvent findPathToRoll(Predicate<Expectation> expectationFilter) {
        String msg = "find path to roll";
        int depth = enterStack(msg);
        Optional<ActionEvent> any = allPaths.stream().filter(p -> isSatisfied(p.getExpectation(), expectationFilter))
                .sorted(Comparator.comparingInt(p -> {
                    int unsatisfiedDegree = p.getUnsatisfiedDegree(actionTimeFrame, true);
                    Logger.getDefault().v("compare unsatisfied degree: %s - %s", unsatisfiedDegree, getPresentation(p));
                    return unsatisfiedDegree;
                })).map(path -> roll(path)).filter(a -> a != null).findAny();
        if (any.isPresent()) {
            exitStack(depth, false, msg);
            return any.get();
        }
        exitStack(depth, true, msg);
        return null;
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

    <V> ActionEvent findPathToRoll(BiPredicate<Property<V>, V> endStateFilter) {
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
        throw new NullPointerException(String.format("unable to check state: %s", getPresentation(obtainable)));
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