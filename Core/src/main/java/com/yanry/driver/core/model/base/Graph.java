/**
 *
 */
package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.base.Expectation.VerifyResult;
import com.yanry.driver.core.model.communicator.Communicator;
import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.event.ExpectationEvent;
import com.yanry.driver.core.model.event.TransitionEvent;
import com.yanry.driver.core.model.runtime.fetch.Obtainable;
import com.yanry.driver.core.model.state.Equals;
import com.yanry.driver.core.model.state.State;
import lib.common.model.log.LogLevel;
import lib.common.model.log.Logger;
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
    private AtomicInteger stackDepth;
    private Set<Expectation> pendingExpectations;
    private ArrayList<Path> rollingPaths;
    private HashSet<ActionEvent> invalidActions;
    private List<Path> concernedPaths;

    public Graph() {
        this.communicators = new LinkedList<>();
        allPaths = new LinkedList<>();
        records = new LinkedList<>();
        propertyCache = new HashMap<>();
        verifiedPaths = new HashSet<>();
        stackDepth = new AtomicInteger();
        pendingExpectations = new HashSet<>();
        rollingPaths = new ArrayList<>();
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
        rollingPaths.clear();
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
            ActionEvent actionEvent;
            while (isTraversing && (actionEvent = roll(path)) != null && isTraversing) {
                if (postAction(actionEvent) && verifiedPaths.contains(path)) {
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
            ActionEvent actionEvent = property.switchTo(valuePredicate);
            if (actionEvent == null) {
                break;
            } else {
                postAction(actionEvent);
            }
        }
        return valuePredicate.test(property.getCurrentValue());
    }

    public <V> boolean achieveState(Property<V> property, V value) {
        boolean success = setup() && achieveStatePredicate(property, new Equals<>(value));
        isTraversing = false;
        return success;
    }

    public boolean postAction(ActionEvent actionEvent) {
        records.add(actionEvent);
        for (Communicator communicator : communicators) {
            if (communicator.performAction(actionEvent)) {
                actionTimeFrame = System.currentTimeMillis();
                if (actionEvent instanceof ExpectationEvent) {
                    verifyExpectation(((ExpectationEvent) actionEvent).getExpectation());
                }
                allPaths.stream().filter(path -> {
                    if (path.getEvent().equals(actionEvent) && path.getUnsatisfiedDegree(actionTimeFrame, false) == 0) {
                        path.getExpectation().preVerify();
                        return true;
                    }
                    return false;
                }).forEach(path -> verify(path));
                // process pending expectation
                if (pendingExpectations.size() > 0) {
                    ArrayList<Expectation> pending = new ArrayList<>(pendingExpectations);
                    for (Expectation expectation : pending) {
                        expectation.preVerify();
                    }
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
        Logger.getDefault().e("cannot perform action: %s", getPresentation(actionEvent));
        invalidActions.add(actionEvent);
        return false;
    }

    private ActionEvent roll(Path path) {
        // 判断是否进入循环
        int lastOccurrence = rollingPaths.lastIndexOf(path);
        if (lastOccurrence >= 0) {
            int lastIndex = rollingPaths.size() - 1;
            if (lastOccurrence == lastIndex) {
                return null;
            }
            int distance = rollingPaths.size() - lastOccurrence;
            boolean isInLoop = true;
            for (int i = lastIndex; i > lastOccurrence && i - distance >= 0; i--) {
                if (!rollingPaths.get(i).equals(rollingPaths.get(i - distance))) {
                    isInLoop = false;
                    break;
                }
            }
            if (isInLoop) {
                return null;
            }
        }
        rollingPaths.add(path);
        // make sure context states are satisfied.
        for (Property property : path.context.keySet()) {
            ValuePredicate valuePredicate = path.context.get(property);
            if (!valuePredicate.test(property.getCurrentValue())) {
                return property.switchTo(valuePredicate);
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
                Logger.getDefault().v("switch state event(from): %s", getPresentation(event));
                return property.switchTo(event.getFrom());
            }
            Logger.getDefault().v("switch state event(to): %s", getPresentation(event));
            return property.switchTo(event.getTo());
        } else if (inputEvent instanceof ActionEvent) {
            ActionEvent actionEvent = (ActionEvent) inputEvent;
            if (isValidAction(actionEvent)) {
                return actionEvent;
            }
        }
        return null;
    }

    private void verify(Path path) {
        int depth = enterStack(getPresentation(path).toString());
        verifiedPaths.add(path);
        Expectation expectation = path.getExpectation();
        VerifyResult result = verifyExpectation(expectation);
        exitStack(depth, result == VerifyResult.Failed, result.name());
    }

    private VerifyResult verifyExpectation(Expectation expectation) {
        VerifyResult result = expectation.verify(this);
        if (result != VerifyResult.Pending) {
            if (expectation.isNeedCheck()) {
                records.add(expectation);
            }
        }
        return result;
    }

    // 兄弟路径指的是当前路径触发时顺带触发的其他路径；父路径是指由状态变迁形成的路径触发时本身形成状态变迁事件，由此导致触发的其他路径。
    <V> void verifySuperPaths(Property<V> property, V from, V to) {
        if (!to.equals(from)) {
            allPaths.stream().filter(path -> {
                if (path.getEvent().matches(property, from, to) && path.getUnsatisfiedDegree(actionTimeFrame, false) == 0) {
                    path.getExpectation().preVerify();
                    return true;
                }
                return false;
            }).forEach(path -> verify(path));
        }
    }

    ActionEvent findPathToRoll(Predicate<Expectation> expectationFilter) {
        int depth = enterStack(null);
        Optional<ActionEvent> any = allPaths.stream().filter(p -> isSatisfied(p.getExpectation(), expectationFilter))
                .sorted(Comparator.comparingInt(p -> p.getUnsatisfiedDegree(actionTimeFrame, true)))
                .map(path -> roll(path))
                .filter(a -> a != null)
                .findAny();
        if (any.isPresent()) {
            ActionEvent actionEvent = any.get();
            exitStack(depth, false, getPresentation(actionEvent).toString());
            return actionEvent;
        }
        exitStack(depth, true, "no path found.");
        return null;
    }

    private boolean isSatisfied(Expectation expectation, Predicate<Expectation> expectationFilter) {
        if (expectationFilter.test(expectation)) {
            return true;
        }
        return expectation.getFollowingExpectations().stream().anyMatch(exp -> isSatisfied(exp, expectationFilter));
    }

    boolean isValidAction(ActionEvent actionEvent) {
        return !invalidActions.contains(actionEvent);
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

    private int enterStack(String msg) {
        int depth = stackDepth.incrementAndGet();
        Logger.getDefault().log(1, LogLevel.Verbose, "+%s: %s", depth, msg);
        return depth;
    }

    private void exitStack(int depth, boolean isError, String msg) {
        if (isError) {
            Logger.getDefault().log(1, LogLevel.Error, "-%s: %s", depth, msg);
        } else {
            Logger.getDefault().log(1, LogLevel.Verbose, "-%s: %s", depth, msg);
        }
        stackDepth.decrementAndGet();
    }
}