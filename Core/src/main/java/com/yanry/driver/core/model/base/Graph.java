/**
 *
 */
package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.base.Expectation.VerifyResult;
import com.yanry.driver.core.model.communicator.Communicator;
import com.yanry.driver.core.model.event.SwitchStateAction;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.core.model.runtime.fetch.Obtainable;
import lib.common.model.log.LogLevel;
import lib.common.model.log.Logger;
import lib.common.util.CollectionUtil;

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
    private List<Path> allPaths;
    Map<Property, Object> propertyCache;
    private List<Object> records;
    private boolean isTraversing;
    HashSet<Property> nullCache;
    private long actionTimeFrame;
    private Set<Path> verifiedPaths;
    private AtomicInteger methodStack;
    private Set<Expectation> pendingExpectations;
    private HashSet<ExternalEvent> invalidActions;
    private List<Path> concernedPaths;
    private LinkedList<State> stateTrace;
    private List<Path> unprocessedPaths;
    private Communicator communicator;

    public Graph() {
        allPaths = new LinkedList<>();
        unprocessedPaths = new LinkedList<>();
        records = new LinkedList<>();
        verifiedPaths = new HashSet<>();
        methodStack = new AtomicInteger();
        pendingExpectations = new HashSet<>();
        invalidActions = new HashSet<>();
        stateTrace = new LinkedList<>();
        propertyCache = new HashMap<>();
        nullCache = new HashSet<>();
    }

    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
    }

    public Path createPath(Event event, Expectation expectation) {
        Path path = new Path(event, expectation);
        allPaths.add(path);
        return path;
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
        stateTrace.clear();
        unprocessedPaths.clear();
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
        if (!setup()) {
            return null;
        }
        for (Path concernedPath : concernedPaths) {
            if (pathFilter == null || pathFilter.test(concernedPath)) {
                unprocessedPaths.add(concernedPath);
            }
        }
        return traverse();
    }

    public List<Object> traverse(int[] selectedIndexes) {
        if (!setup()) {
            return null;
        }
        if (selectedIndexes == null) {
            unprocessedPaths.addAll(concernedPaths);
        } else {
            for (int i : selectedIndexes) {
                unprocessedPaths.add(concernedPaths.get(i));
            }
        }
        return traverse();
    }

    private List<Object> traverse() {
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
            Logger.getDefault().dd("traverse path: ", path);
            ExternalEvent externalEvent;
            while (isTraversing && (externalEvent = getNextAction(path)) != null && isTraversing) {
                if (postAction(externalEvent) && verifiedPaths.contains(path)) {
                    break;
                }
            }
            unprocessedPaths.removeAll(verifiedPaths);
            if (unprocessedPaths.remove(path)) {
                Logger.getDefault().ww("fail to traverse path: ", path);
            }
            verifiedPaths.clear();
            stateTrace.clear();
        }
        // handle pending expectation
        Iterator<Expectation> iterator = pendingExpectations.iterator();
        while (iterator.hasNext()) {
            Expectation expectation = iterator.next();
            Logger.getDefault().vv("verify pending expectation: ", expectation);
            State trigger = expectation.getTrigger();
            if (!achieveStatePredicate(trigger.getProperty(), trigger.getValuePredicate())) {
                Logger.getDefault().ww("fail to trigger pending expectation: ", expectation);
            }
        }
        List<Object> result = new ArrayList<>(records);
        isTraversing = false;
        return result;
    }

    private ExternalEvent getNextAction(Path pathToTraverse) {
        State selectedState = null;
        Iterator<State> iterator = stateTrace.iterator();
        boolean found = false;
        while (iterator.hasNext()) {
            if (found) {
                iterator.next();
                iterator.remove();
            } else {
                State state = iterator.next();
                if (state.isSatisfied()) {
                    found = true;
                    iterator.remove();
                } else {
                    selectedState = state;
                }
            }
        }
        if (selectedState == null) {
            return roll(pathToTraverse);
        }
        return selectedState.getProperty().switchTo(selectedState.getValuePredicate());
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
        if (communicator != null) {
            if (communicator.performAction(externalEvent)) {
                actionTimeFrame = System.currentTimeMillis();
                if (externalEvent instanceof SwitchStateAction) {
                    SwitchStateAction stateAction = (SwitchStateAction) externalEvent;
                    Property property = stateAction.getProperty();
                    property.handleExpectation(stateAction.getValue(), false);
                }
                for (Path path : new ArrayList<>(allPaths)) {
                    if (path.getEvent().equals(externalEvent) && path.getUnsatisfiedDegree(actionTimeFrame, false) == 0) {
                        verify(path);
                    }
                }
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
        Logger.getDefault().ee("cannot perform action: ", externalEvent);
        invalidActions.add(externalEvent);
        return false;
    }

    private ExternalEvent roll(Path path) {
        enterMethod(path);
        // make sure context states are satisfied.
        for (Property property : path.getContext().keySet()) {
            ValuePredicate valuePredicate = path.getContext().get(property);
            if (!valuePredicate.test(property.getCurrentValue())) {
                stateTrace.add(property.getState(valuePredicate));
                ExternalEvent externalEvent = property.switchTo(valuePredicate);
                exitMethod(LogLevel.Verbose, externalEvent);
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
            ValuePredicate from = event.getFrom();
            if (!from.test(property.getCurrentValue())) {
                stateTrace.add(property.getState(from));
                ExternalEvent externalEvent = property.switchTo(from);
                exitMethod(LogLevel.Verbose, externalEvent);
                return externalEvent;
            }
            ValuePredicate to = event.getTo();
            stateTrace.add(property.getState(to));
            ExternalEvent externalEvent = property.switchTo(to);
            exitMethod(LogLevel.Verbose, externalEvent);
            return externalEvent;
        } else if (inputEvent instanceof ExternalEvent) {
            ExternalEvent externalEvent = (ExternalEvent) inputEvent;
            if (isValidAction(externalEvent)) {
                exitMethod(LogLevel.Verbose, externalEvent);
                return externalEvent;
            }
        }
        exitMethod(LogLevel.Warn, "no available action.");
        return null;
    }

    private void verify(Path path) {
        verifiedPaths.add(path);
        verifyExpectation(path.getExpectation());
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

    <V> void verifySuperPaths(Property<V> property, V from, V to) {
        if (!Objects.equals(from, to)) {
            for (Path path : new ArrayList<>(allPaths)) {
                Event event = path.getEvent();
                if (event instanceof InternalEvent) {
                    // 父路径是指由状态变迁形成的路径触发时本身形成状态变迁事件，由此导致触发的其他路径。
                    if (((InternalEvent) event).matches(property, from, to) && path.getUnsatisfiedDegree(actionTimeFrame, false) == 0) {
                        verify(path);
                    }
                }
            }
        }
    }

    ExternalEvent findPathToRoll(Predicate<Expectation> expectationFilter) {
        List<Path> sorted = allPaths.stream().filter(p -> isSatisfied(p.getExpectation(), expectationFilter))
                .sorted(Comparator.comparingInt(p -> {
                    int unsatisfiedDegree = p.getUnsatisfiedDegree(actionTimeFrame, true);
                    if (unprocessedPaths.contains(p)) {
                        unsatisfiedDegree--;
                    }
                    return unsatisfiedDegree;
                }))
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
            Logger.getDefault().ii("action is invalid: ", externalEvent);
            return false;
        }
        if (CollectionUtil.checkLoop(records, externalEvent)) {
            Logger.getDefault().ii("skip action to avoid loop: ", externalEvent);
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
        if (communicator != null) {
            V value = communicator.fetchState(obtainable);
            property.communicateTimeFrame = actionTimeFrame;
            return value;
        }
        Logger.getDefault().ww("unable to check state: ", obtainable);
        return null;
    }

    Boolean verifyExpectation(NonPropertyExpectation expectation) {
        if (communicator != null) {
            Boolean result = communicator.verifyExpectation(expectation);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    void enterMethod(Object msg) {
        Logger.getDefault().concat(1, LogLevel.Verbose, '+', methodStack.incrementAndGet(), ':', msg);
    }

    void exitMethod(LogLevel logLevel, Object msg) {
        int depth = methodStack.getAndDecrement();
        Logger.getDefault().concat(1, logLevel, '-', depth, ':', msg);
    }
}