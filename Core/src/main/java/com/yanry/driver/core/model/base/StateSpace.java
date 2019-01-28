/**
 *
 */
package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.base.Expectation.VerifyResult;
import com.yanry.driver.core.model.event.SwitchStateAction;
import com.yanry.driver.core.model.libtemp.ThreadSafeExecutor;
import com.yanry.driver.core.model.libtemp.revert.*;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.core.model.runtime.Watcher;
import com.yanry.driver.core.model.runtime.communicator.Communicator;
import com.yanry.driver.core.model.runtime.fetch.Obtainable;
import com.yanry.driver.core.model.runtime.record.ActionRecord;
import com.yanry.driver.core.model.runtime.record.CommunicateRecord;
import com.yanry.driver.core.model.runtime.record.VerificationRecord;
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
public class StateSpace extends RevertManager {
    private static AtomicInteger sequenceGenerator = new AtomicInteger();
    private ThreadSafeExecutor executor;

    private LinkedList<Path> allPaths;
    private List<Path> concernedPaths;
    private Communicator communicator;
    private Watcher watcher;

    private boolean isTraversing;
    private AtomicInteger methodStack;

    private RevertibleMap<Property, Object> cache;
    private RevertibleLinkedList<CommunicateRecord> records;
    private RevertibleLong frameMark;
    private RevertibleBoolean hasChanged;
    private RevertibleSet<Path> verifiedPaths;
    private RevertibleSet<Expectation> pendingExpectations;
    private RevertibleSet<ExternalEvent> invalidActions;
    private RevertibleLinkedList<State> stateTrace;
    private RevertibleLinkedList<Path> unprocessedPaths;

    public StateSpace() {
        allPaths = new LinkedList<>();
        unprocessedPaths = new RevertibleLinkedList<>(this);
        records = new RevertibleLinkedList<>(this);
        verifiedPaths = new RevertibleSet<>(this);
        methodStack = new AtomicInteger();
        pendingExpectations = new RevertibleSet<>(this);
        invalidActions = new RevertibleSet<>(this);
        stateTrace = new RevertibleLinkedList<>(this);
        cache = new RevertibleMap<>(this);
        frameMark = new RevertibleLong(this);
        hasChanged = new RevertibleBoolean(this);
        executor = new ThreadSafeExecutor();
        Thread workThread = new Thread(executor);
        workThread.setName(String.format("%s-%s", StateSpace.class.getSimpleName(), sequenceGenerator.getAndIncrement()));
        workThread.start();
    }

    public void resetStates() {
        this.clean();
        cache.clear();
        records.clear();
        frameMark.set(0);
        verifiedPaths.clear();
        pendingExpectations.clear();
        invalidActions.clear();
        stateTrace.clear();
        unprocessedPaths.clear();
    }

    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
    }

    public void setWatcher(Watcher watcher) {
        this.watcher = watcher;
    }

    public Path createPath(Event event, Expectation expectation) {
        Path path = new Path(event, expectation);
        allPaths.add(path);
        return path;
    }

    void addPendingExpectation(Expectation expectation) {
        pendingExpectations.add(expectation);
    }

    void addStateTrace(State state) {
        stateTrace.addLast(state);
    }

    long getFrameMark() {
        return frameMark.get();
    }

    RevertibleMap<Property, Object> getCache() {
        return cache;
    }

    ThreadSafeExecutor getExecutor() {
        return executor;
    }

    void post(Runnable runnable) {
        executor.post(runnable);
    }

    private boolean needCheck(Expectation expectation) {
        return expectation.isNeedCheck() || expectation.getFollowingExpectations().parallelStream().anyMatch(this::needCheck);
    }

    public void abort() {
        isTraversing = false;
    }

    public void release() {
        executor.stop();
    }

    private boolean setup() {
        if (isTraversing) {
            return false;
        }
        isTraversing = true;
        this.clean();
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

    public ArrayList<CommunicateRecord> traverse(Predicate<Path> pathFilter) {
        return executor.execute(() -> {
            if (!setup()) {
                return null;
            }
            for (Path concernedPath : concernedPaths) {
                if (pathFilter == null || pathFilter.test(concernedPath)) {
                    unprocessedPaths.addLast(concernedPath);
                }
            }
            return traverse();
        });
    }

    public ArrayList<CommunicateRecord> traverse(int[] selectedIndexes) {
        return executor.execute(() -> {
            if (!setup()) {
                return null;
            }
            if (selectedIndexes == null) {
                unprocessedPaths.addAll(concernedPaths);
            } else {
                for (int i : selectedIndexes) {
                    unprocessedPaths.addLast(concernedPaths.get(i));
                }
            }
            return traverse();
        });
    }

    private ArrayList<CommunicateRecord> traverse() {
        int size = unprocessedPaths.size();
        while (!unprocessedPaths.isEmpty() && isTraversing) {
            Path path = null;
            int minDegree = Integer.MAX_VALUE;
            for (Path p : unprocessedPaths.getList()) {
                int unsatisfiedDegree = p.getUnsatisfiedDegree(frameMark.get(), true);
                if (unsatisfiedDegree == 0) {
                    path = p;
                    break;
                } else if (unsatisfiedDegree < minDegree) {
                    path = p;
                    minDegree = unsatisfiedDegree;
                }
            }
            Logger.getDefault().d("traverse path %s/%s: %s", size - unprocessedPaths.size() + 1, size, path);
            ExternalEvent externalEvent;
            while (isTraversing && (externalEvent = getNextAction(path)) != null && isTraversing) {
                if (postAction(externalEvent) && verifiedPaths.contains(path)) {
                    break;
                }
            }
            unprocessedPaths.removeAll(verifiedPaths.getList());
            if (unprocessedPaths.remove(path)) {
                Logger.getDefault().ww("fail to traverse path: ", path);
            }
            verifiedPaths.clear();
            stateTrace.clear();
        }
        // handle pending expectation
        for (Expectation expectation : pendingExpectations.getList()) {
            Logger.getDefault().vv("verify pending expectation: ", expectation);
            State trigger = expectation.getTrigger();
            if (!achieveStatePredicate(trigger.getProperty(), trigger.getValuePredicate())) {
                Logger.getDefault().ww("fail to trigger pending expectation: ", expectation);
            }
        }
        ArrayList<CommunicateRecord> result = records.getList();
        isTraversing = false;
        return result;
    }

    private ExternalEvent getNextAction(Path pathToTraverse) {
        State selectedState = null;
        Iterator<State> iterator = stateTrace.iterator();
        boolean found = false;
        while (iterator.hasNext()) {
            State state = iterator.next();
            if (found) {
                iterator.remove();
            } else {
                if (state.isSatisfied()) {
                    found = true;
                    iterator.remove();
                } else {
                    selectedState = state;
                }
            }
        }
        ActionCollector actionCollector = new ActionCollector(1);
        if (selectedState == null) {
            roll(pathToTraverse, actionCollector);
        } else {
            selectedState.getProperty().switchTo(selectedState.getValuePredicate(), actionCollector);
        }
        return actionCollector.pop();
    }

    private <V> boolean achieveStatePredicate(Property<V> property, ValuePredicate<V> valuePredicate) {
        while (isTraversing && !valuePredicate.test(property.getCurrentValue())) {
            ActionCollector actionCollector = new ActionCollector(1);
            property.switchTo(valuePredicate, actionCollector);
            if (actionCollector.isEmpty()) {
                break;
            } else {
                postAction(actionCollector.pop());
            }
        }
        return valuePredicate.test(property.getCurrentValue());
    }

    public <V> boolean achieveState(Property<V> property, V value) {
        return executor.execute(() -> {
            boolean success = setup() && achieveStatePredicate(property, Equals.of(value));
            isTraversing = false;
            return success;
        });
    }

    public void fire(ExternalEvent event) {
        executor.execute(() -> doFire(event));
    }

    public void fireLater(ExternalEvent event) {
        executor.post(() -> doFire(event));
    }

    private void doFire(ExternalEvent event) {
        Logger.getDefault().dd(event);
        frameMark.increment();
        if (event instanceof SwitchStateAction) {
            SwitchStateAction stateAction = (SwitchStateAction) event;
            Property property = stateAction.getProperty();
            property.handleExpectation(stateAction.getValue(), false);
        }
        // 为了避免使用相同事件来切换状态（如播放/暂停）的路径被连续触发，所以先把path放入容器中。
        LinkedList<Path> pathsToVerify = new LinkedList<>();
        for (Path path : new ArrayList<>(allPaths)) {
            if (path.getEvent().equals(event) && path.getUnsatisfiedDegree(frameMark.get(), false) == 0) {
                pathsToVerify.add(path);
            }
        }
        for (Path path : pathsToVerify) {
            verify(path);
        }
        // process pending expectation
        if (!pendingExpectations.isEmpty()) {
            for (Expectation expectation : pendingExpectations.getList()) {
                VerifyResult result = expectation.verify(this);
                if (result != VerifyResult.Pending) {
                    if (expectation.isNeedCheck()) {
                        records.addLast(new VerificationRecord(expectation, result));
                    }
                    pendingExpectations.remove(expectation);
                }
            }
        }
        if (watcher != null && hasChanged.get()) {
            watcher.onTransitionComplete();
            hasChanged.set(false);
        }
    }

    private boolean postAction(ExternalEvent event) {
        String snapShoot = cache.getSnapShootMD5();
        if (communicator != null) {
            LinkedList<Runnable> preActions = event.getPreActions();
            if (preActions != null) {
                for (Runnable action : preActions) {
                    action.run();
                }
            }
            if (communicator.performAction(event)) {
                records.addLast(new ActionRecord(event, true, snapShoot));
                fire(event);
                return true;
            }
        }
        records.addLast(new ActionRecord(event, false, snapShoot));
        Logger.getDefault().ee("cannot perform action: ", event);
        invalidActions.add(event);
        return false;
    }

    private void roll(Path path, ActionCollector actionCollector) {
        enterMethod(path);
        // make sure context states are satisfied.
        Context context = path.getContext();
        if (!context.isSatisfied()) {
            context.trySatisfy(actionCollector, this);
            exitMethod(LogLevel.Verbose, actionCollector);
            return;
        }
        // all context states are satisfied by now.
        // trigger event
        Event inputEvent = path.getEvent();
        if (inputEvent instanceof InternalEvent) {
            ((InternalEvent) inputEvent).traverse(actionCollector);
            exitMethod(LogLevel.Verbose, actionCollector);
            return;
        } else if (inputEvent instanceof ExternalEvent) {
            ExternalEvent externalEvent = (ExternalEvent) inputEvent;
            Context precondition = externalEvent.getPrecondition();
            if (precondition != null && !precondition.isSatisfied()) {
                precondition.trySatisfy(actionCollector, this);
                exitMethod(LogLevel.Verbose, actionCollector);
                return;
            }
            if (isValidAction(externalEvent)) {
                actionCollector.add(externalEvent, this);
                exitMethod(LogLevel.Verbose, actionCollector);
                return;
            }
        }
        exitMethod(LogLevel.Warn, "no available action.");
    }

    private void verify(Path path) {
        verifiedPaths.add(path);
        verifyExpectation(path.getExpectation());
    }

    private VerifyResult verifyExpectation(Expectation expectation) {
        VerifyResult result = expectation.verify(this);
        if (result != VerifyResult.Pending) {
            if (expectation.isNeedCheck()) {
                records.addLast(new VerificationRecord(expectation, result));
            }
        }
        return result;
    }

    <V> void verifySuperPaths(Property<V> property, V from, V to) {
        if (!Objects.equals(from, to)) {
            if (watcher != null) {
                hasChanged.set(true);
                watcher.onStateChange(property, from, to);
            }
            for (Path path : new ArrayList<>(allPaths)) {
                Event event = path.getEvent();
                if (event instanceof InternalEvent) {
                    // 父路径是指由状态变迁形成的路径触发时本身形成状态变迁事件，由此导致触发的其他路径。
                    if (((InternalEvent) event).matches(property, from, to) && path.getUnsatisfiedDegree(frameMark.get(), false) == 0) {
                        verify(path);
                    }
                }
            }
        }
    }

    void findPathToRoll(Predicate<Expectation> expectationFilter, ActionCollector actionCollector) {
        List<Path> sorted = allPaths.stream().filter(p -> isSatisfied(p.getExpectation(), expectationFilter))
                .sorted(Comparator.comparingInt(p -> {
                    int unsatisfiedDegree = p.getUnsatisfiedDegree(frameMark.get(), true);
                    if (unprocessedPaths.contains(p)) {
                        // 优先处理未处理过的path
                        unsatisfiedDegree--;
                    }
                    return unsatisfiedDegree;
                })).collect(Collectors.toList());
        for (Path path : sorted) {
            if (actionCollector.isFull()) {
                return;
            }
            roll(path, actionCollector);
        }
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
        if (CollectionUtil.checkLoop(records.getList(), new ActionRecord(externalEvent, false, cache.getSnapShootMD5()))) {
            Logger.getDefault().ii("skip action to avoid loop: ", externalEvent);
            return false;
        }
        return true;
    }

    public <V> V obtainValue(Obtainable<V> obtainable) {
        return executor.execute(() -> {
            Property<V> property = obtainable.getProperty();
            if (property.isValueFresh()) {
                return property.getCurrentValue();
            }
            if (communicator != null) {
                V value = communicator.fetchState(obtainable);
                property.setStateSpaceFrameMark(frameMark.get());
                return value;
            }
            Logger.getDefault().ww("unable to check state: ", obtainable);
            return null;
        });
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
        Logger.getDefault().concat(1, logLevel, '-', depth, ':', ObjectUtil.getPresentation(msg));
    }
}
