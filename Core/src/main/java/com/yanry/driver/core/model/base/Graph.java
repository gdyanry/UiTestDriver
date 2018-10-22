/**
 *
 */
package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.base.Expectation.VerifyResult;
import com.yanry.driver.core.model.communicator.Communicator;
import com.yanry.driver.core.model.event.ExpectationEvent;
import com.yanry.driver.core.model.event.ExternalEvent;
import com.yanry.driver.core.model.event.TransitionEvent;
import com.yanry.driver.core.model.runtime.fetch.Obtainable;
import com.yanry.driver.core.model.state.Equals;
import com.yanry.driver.core.model.state.State;
import lib.common.model.log.LogLevel;
import lib.common.model.log.Logger;
import lib.common.util.CollectionUtil;
import lib.common.util.object.ObjectUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    private List<Object> records;
    private boolean isTraversing;
    private List<Communicator> communicators;
    Map<Property, Object> propertyCache;
    private long actionTimeFrame;
    private Set<Path> verifiedPaths;
    private AtomicInteger methodStack;
    private Set<Expectation> pendingExpectations;
    private HashSet<ExternalEvent> invalidActions;
    private List<Path> concernedPaths;

    public Graph() {
        this.communicators = new LinkedList<>();
        allPaths = new LinkedList<>();
        records = new LinkedList<>();
        propertyCache = new HashMap<>();
        verifiedPaths = new HashSet<>();
        methodStack = new AtomicInteger();
        pendingExpectations = new HashSet<>();
        invalidActions = new HashSet<>();
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

    private boolean needCheck(Expectation expectation) {
        return expectation.isNeedCheck() || expectation.getFollowingExpectations().parallelStream().anyMatch(exp -> needCheck(exp));
    }

    /**
     * This should be called from a different thread.
     */
    public void abort() {
        isTraversing = false;
    }

    private boolean setup() {
        if (isTraversing) {
            return false;
        }
        isTraversing = true;
        records.clear();
        verifiedPaths.clear();
        return true;
    }

    public List<Path> getConcernedPaths() {
        if (concernedPaths == null) {
            concernedPaths = allPaths.parallelStream()
                    .filter(p -> needCheck(p.getExpectation()))
                    .collect(Collectors.toList());
        }
        return concernedPaths;
    }

    public List<Object> traverse(Predicate<Path> pathFilter) {
        List<Path> unprocessedPaths = concernedPaths.parallelStream()
                .filter(p -> pathFilter == null || pathFilter.test(p))
                .collect(Collectors.toList());
        return traverse(unprocessedPaths);
    }

    public List<Object> traverse(int[] selectedIndexes) {
        List<Path> unprocessedPaths;
        if (selectedIndexes == null) {
            unprocessedPaths = new ArrayList<>(concernedPaths);
        } else {
            unprocessedPaths = new ArrayList<>(selectedIndexes.length);
            for (int i : selectedIndexes) {
                unprocessedPaths.add(concernedPaths.get(i));
            }
        }
        return traverse(unprocessedPaths);
    }

    private List<Object> traverse(List<Path> unprocessedPaths) {
        if (!setup()) {
            return null;
        }
        while (!unprocessedPaths.isEmpty() && isTraversing) {
            Path path = null;
            int minDegree = Integer.MAX_VALUE;
            for (Path p : unprocessedPaths) {
                int unsatisfiedDegree = p.getUnsatisfiedDegree(actionTimeFrame, true);
                if (unsatisfiedDegree == 0) {
                    path = p;
                    break;
                } else if (unsatisfiedDegree < minDegree) {
                    path = p;
                    minDegree = unsatisfiedDegree;
                }
            }
            Logger.getDefault().d("traverse path: %s", getPresentation(path));
            ExternalEvent externalEvent;
            while (isTraversing && (externalEvent = roll(path)) != null && isTraversing) {
                if (postAction(externalEvent) && verifiedPaths.contains(path)) {
                    break;
                }
            }
            unprocessedPaths.removeAll(verifiedPaths);
            if (unprocessedPaths.remove(path)) {
                Logger.getDefault().e("fail to traverse path: %s", getPresentation(path));
            }
            verifiedPaths.clear();
        }
        // handle pending expectation
        Iterator<Expectation> iterator = pendingExpectations.iterator();
        while (iterator.hasNext()) {
            Expectation expectation = iterator.next();
            Logger.getDefault().v("verify pending expectation: %s", expectation);
            State trigger = expectation.getTrigger();
            if (!achieveStatePredicate(trigger.getProperty(), trigger.getValuePredicate())) {
                Logger.getDefault().e("fail to trigger pending expectation: %s", expectation);
            }
        }
        List<Object> result = new ArrayList<>(records);
        isTraversing = false;
        return result;
    }

    private <V> boolean achieveStatePredicate(Property<V> property, ValuePredicate<V> valuePredicate) {
        while (isTraversing && !valuePredicate.test(property.getCurrentValue())) {
            ExternalEvent externalEvent = property.switchTo(valuePredicate);
            if (externalEvent == null) {
                break;
            } else {
                postAction(externalEvent);
            }
        }
        return valuePredicate.test(property.getCurrentValue());
    }

    public <V> boolean achieveState(Property<V> property, V value) {
        boolean success = setup() && achieveStatePredicate(property, new Equals<>(value));
        isTraversing = false;
        return success;
    }

    public boolean postAction(ExternalEvent externalEvent) {
        records.add(externalEvent);
        for (Communicator communicator : communicators) {
            if (communicator.performAction(externalEvent)) {
                actionTimeFrame = System.currentTimeMillis();
                if (externalEvent instanceof ExpectationEvent) {
                    verifyExpectation(((ExpectationEvent) externalEvent).getExpectation());
                }
                allPaths.stream().filter(path -> path.getEvent().equals(externalEvent) && path.getUnsatisfiedDegree(actionTimeFrame, false) == 0)
                        .collect(Collectors.toList())
                        .forEach(path -> verify(path));
                // process pending expectation
                if (pendingExpectations.size() > 0) {
                    ArrayList<Expectation> pending = new ArrayList<>(pendingExpectations);
                    for (Expectation expectation : pending) {
                        VerifyResult result = expectation.verify(this);
                        if (result != VerifyResult.Pending) {
                            if (expectation.isNeedCheck()) {
                                records.add(expectation);
                            }
                            pendingExpectations.remove(expectation);
                        }
                    }
                }
                return true;
            }
        }
        Logger.getDefault().e("cannot perform action: %s", getPresentation(externalEvent));
        invalidActions.add(externalEvent);
        return false;
    }

    private ExternalEvent roll(Path path) {
        int depth = enterMethod(getPresentation(path).toString());
        // make sure context states are satisfied.
        for (Property property : path.context.keySet()) {
            ValuePredicate valuePredicate = path.context.get(property);
            if (!valuePredicate.test(property.getCurrentValue())) {
                ExternalEvent externalEvent = property.switchTo(valuePredicate);
                exitMethod(depth, false, getPresentation(externalEvent).toString());
                return externalEvent;
            }
        }
        // all context states are satisfied by now.
        // trigger event
        Event inputEvent = path.getEvent();
        if (inputEvent instanceof TransitionEvent) {
            TransitionEvent event = (TransitionEvent) inputEvent;
            // roll path for this switch event.
            Property property = event.getProperty();
            if (event.getFrom() != null && !event.getFrom().test(property.getCurrentValue())) {
                ExternalEvent externalEvent = property.switchTo(event.getFrom());
                exitMethod(depth, false, getPresentation(externalEvent).toString());
                return externalEvent;
            }
            ExternalEvent externalEvent = property.switchTo(event.getTo());
            exitMethod(depth, false, getPresentation(externalEvent).toString());
            return externalEvent;
        } else if (inputEvent instanceof ExternalEvent) {
            ExternalEvent externalEvent = (ExternalEvent) inputEvent;
            if (isValidAction(externalEvent)) {
                exitMethod(depth, false, getPresentation(externalEvent).toString());
                return externalEvent;
            }
        }
        exitMethod(depth, true, "no available action.");
        return null;
    }

    private void verify(Path path) {
        verifiedPaths.add(path);
        verifyExpectation(path.getExpectation());
    }

    private VerifyResult verifyExpectation(Expectation expectation) {
        int depth = enterMethod(getPresentation(expectation).toString());
        VerifyResult result = expectation.verify(this);
        if (result != VerifyResult.Pending) {
            if (expectation.isNeedCheck()) {
                records.add(expectation);
            }
        }
        exitMethod(depth, false, result.name());
        return result;
    }

    // 兄弟路径指的是当前路径触发时顺带触发的其他路径；父路径是指由状态变迁形成的路径触发时本身形成状态变迁事件，由此导致触发的其他路径。
    <V> void verifySuperPaths(Property<V> property, V from, V to) {
        if (!to.equals(from)) {
            allPaths.stream().filter(path -> path.getEvent().matches(property, from, to) && path.getUnsatisfiedDegree(actionTimeFrame, false) == 0)
                    .forEach(path -> verify(path));
        }
    }

    ExternalEvent findPathToRoll(Predicate<Expectation> expectationFilter) {
        List<Path> sorted = allPaths.stream().filter(p -> isSatisfied(p.getExpectation(), expectationFilter))
                .sorted(Comparator.comparingInt(p -> p.getUnsatisfiedDegree(actionTimeFrame, true)))
                .collect(Collectors.toList());
        for (Path path : sorted) {
            ExternalEvent externalEvent = roll(path);
            if (externalEvent != null) {
                return externalEvent;
            }
        }
        return null;
    }

    private boolean isSatisfied(Expectation expectation, Predicate<Expectation> expectationFilter) {
        if (expectationFilter.test(expectation)) {
            return true;
        }
        return expectation.getFollowingExpectations().stream().anyMatch(exp -> isSatisfied(exp, expectationFilter));
    }

    boolean isValidAction(ExternalEvent externalEvent) {
        if (invalidActions.contains(externalEvent)) {
            Logger.getDefault().v("action is invalid: %s", getPresentation(externalEvent));
            return false;
        }
        if (CollectionUtil.checkLoop(records, externalEvent)) {
            Logger.getDefault().v("skip action to avoid loop: %s", getPresentation(externalEvent));
            return false;
        }
        return !invalidActions.contains(externalEvent) && !CollectionUtil.checkLoop(records, externalEvent);
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
            V value = communicator.fetchState(obtainable);
            if (value != null) {
                property.communicateTimeFrame = actionTimeFrame;
                return value;
            }
        }
        Logger.getDefault().e("unable to check state: %s", getPresentation(obtainable));
        return null;
    }

    Boolean verifyExpectation(NonPropertyExpectation expectation) {
        for (Communicator communicator : communicators) {
            Boolean result = communicator.verifyExpectation(expectation);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    int enterMethod(String msg) {
        int depth = methodStack.incrementAndGet();
        Logger.getDefault().log(1, LogLevel.Verbose, "+%s: %s", depth, msg);
        return depth;
    }

    void exitMethod(int depth, boolean isError, String msg) {
        if (isError) {
            Logger.getDefault().log(1, LogLevel.Error, "-%s: %s", depth, msg);
        } else {
            Logger.getDefault().log(1, LogLevel.Verbose, "-%s: %s", depth, msg);
        }
        methodStack.decrementAndGet();
    }
}