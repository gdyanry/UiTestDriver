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
import java.util.function.Consumer;
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
    private LinkedList<State> traversingStateStack;
    private long frameMark;

    private RevertibleMap<Property, Object> cache;
    private RevertibleLinkedList<CommunicateRecord> records;
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
        hasChanged = new RevertibleBoolean(this);
        executor = new ThreadSafeExecutor();
        traversingStateStack = new LinkedList<>();
        Thread workThread = new Thread(executor);
        workThread.setName(String.format("%s-%s", StateSpace.class.getSimpleName(), sequenceGenerator.getAndIncrement()));
        workThread.start();
    }

    public void resetStates() {
        this.clean();
        cache.clear();
        records.clear();
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

    public void printCache(Consumer<String> printer) {
        if (printer != null) {
            printer.accept(cache.toString());
        }
    }

    void addPendingExpectation(Expectation expectation) {
        pendingExpectations.add(expectation);
    }

    void addStateTrace(State state) {
        stateTrace.addLast(state);
    }

    <V> boolean pushTraversingState(State<V> state) {
        if (!traversingStateStack.contains(state)) {
            traversingStateStack.push(state);
            return true;
        }
        return false;
    }

    void popTraversingState() {
        traversingStateStack.pop();
    }

    long getFrameMark() {
        return frameMark;
    }

    RevertibleMap<Property, Object> getCache() {
        return cache;
    }

    ThreadSafeExecutor getExecutor() {
        return executor;
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
        return executor.sync(() -> {
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
        return executor.sync(() -> {
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
                int unsatisfiedDegree = p.getUnsatisfiedDegree(frameMark, true);
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
        if (selectedState == null) {
            return roll(pathToTraverse, null);
        } else {
            return selectedState.getProperty().switchTo(selectedState.getValuePredicate(), null);
        }
    }

    private <V> boolean achieveStatePredicate(Property<V> property, ValuePredicate<V> valuePredicate) {
        while (isTraversing && !valuePredicate.test(property.getCurrentValue())) {
            ExternalEvent externalEvent = property.switchTo(valuePredicate, null);
            if (externalEvent == null) {
                return false;
            } else {
                postAction(externalEvent);
            }
        }
        return valuePredicate.test(property.getCurrentValue());
    }

    public <V> boolean achieveState(Property<V> property, V value) {
        return executor.sync(() -> {
            boolean success = setup() && achieveStatePredicate(property, Equals.of(value));
            isTraversing = false;
            return success;
        });
    }

    public <V> void achieveStateRehearsal(Property<V> property, ValuePredicate<V> predicate, TransitionRehearsal rehearsal) {
        executor.sync(() -> {


            LinkedList<ActionGuard> filterList = new LinkedList<>();
            while (!predicate.test(property.getCurrentValue())) {
                ActionGuard filter = practise(property, predicate, null, filterList);
                if (filter == null) {
                    break;
                }
            }
            revert(filterList.peekFirst());
            return filterList;
        });
    }

    private <V> ActionGuard practise(Property<V> property, ValuePredicate<V> predicate, ActionGuard actionFilter, LinkedList<ActionGuard> filterList) {
        ExternalEvent event = property.switchTo(predicate, actionFilter);
        if (event == null) {
            if (filterList.isEmpty()) {
                return null;
            } else {
                ActionGuard pop = filterList.pop();
                pop.invalidate(pop.getSelectedAction());
                revert(pop);
                return practise(property, predicate, pop, filterList);
            }
        } else {
            if (actionFilter == null) {
                actionFilter = new ActionGuard();
            }
            actionFilter.setSelectedAction(event);
            filterList.push(actionFilter);
            tag(actionFilter);
            doFire(event);
            return actionFilter;
        }
    }

    public boolean syncFire(ExternalEvent event, Context promises) {
        return executor.sync(() -> {
            if (promises == null) {
                doFire(event);
            } else {
                Object tag = new Object();
                tag(tag);
                doFire(event);
                boolean satisfied = promises.isSatisfied();
                revert(tag);
                doFire(event);
                return satisfied;
            }
            return true;
        });
    }

    public void asyncFire(ExternalEvent event) {
        executor.async(() -> doFire(event));
    }

    private void doFire(ExternalEvent event) {
        Logger.getDefault().dd(getTagCount(), ' ', event);
        frameMark++;
        if (event instanceof SwitchStateAction) {
            SwitchStateAction stateAction = (SwitchStateAction) event;
            Property property = stateAction.getProperty();
            property.handleExpectation(stateAction.getValue(), false);
        }
        // 为了避免使用相同事件来切换状态（如播放/暂停）的路径被连续触发，所以先把path放入容器中。
        LinkedList<Path> pathsToVerify = new LinkedList<>();
        for (Path path : new ArrayList<>(allPaths)) {
            if (path.getEvent().equals(event) && path.getUnsatisfiedDegree(frameMark, false) == 0) {
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
        LinkedList<Runnable> preActions = event.getPreActions();
        if (preActions != null) {
            for (Runnable action : preActions) {
                action.run();
            }
        }
        if (getTagCount() == 0 && communicator != null && !communicator.performAction(event)) {
            records.addLast(new ActionRecord(event, false, snapShoot));
            Logger.getDefault().ee("cannot perform action: ", event);
            invalidActions.add(event);
            return false;
        }
        records.addLast(new ActionRecord(event, true, snapShoot));
        doFire(event);
        return true;
    }

    private ExternalEvent roll(Path path, ActionGuard actionGuard) {
        enterMethod(path);
        // make sure context states are satisfied.
        Context context = path.getContext();
        if (!context.isSatisfied()) {
            ExternalEvent event = context.trySatisfy(actionGuard);
            exitMethod(LogLevel.Verbose, event);
            return event;
        }
        // all context states are satisfied by now.
        // trigger event
        Event inputEvent = path.getEvent();
        if (inputEvent instanceof InternalEvent) {
            ExternalEvent event = ((InternalEvent) inputEvent).traverse(actionGuard);
            exitMethod(LogLevel.Verbose, event);
            return event;
        } else if (inputEvent instanceof ExternalEvent) {
            ExternalEvent externalEvent = (ExternalEvent) inputEvent;
            Context precondition = externalEvent.getPrecondition();
            if (precondition != null && !precondition.isSatisfied()) {
                ExternalEvent event = precondition.trySatisfy(actionGuard);
                exitMethod(LogLevel.Verbose, event);
                return event;
            }
            if (isValidAction(externalEvent, actionGuard)) {
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
                    if (((InternalEvent) event).matches(property, from, to) && path.getUnsatisfiedDegree(frameMark, false) == 0) {
                        verify(path);
                    }
                }
            }
        }
    }

    ExternalEvent findPathToRoll(Predicate<Expectation> expectationFilter, ActionGuard actionGuard) {
        List<Path> sorted = allPaths.stream().filter(p -> isSatisfied(p.getExpectation(), expectationFilter))
                .sorted(Comparator.comparingInt(p -> {
                    int unsatisfiedDegree = p.getUnsatisfiedDegree(frameMark, true);
                    if (unprocessedPaths.contains(p)) {
                        // 优先处理未处理过的path
                        unsatisfiedDegree--;
                    }
                    return unsatisfiedDegree;
                })).collect(Collectors.toList());
        for (Path path : sorted) {
            ExternalEvent externalEvent = roll(path, actionGuard);
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

    boolean isValidAction(ExternalEvent externalEvent, ActionGuard actionGuard) {
        if (invalidActions.contains(externalEvent)) {
            Logger.getDefault().ii("action is invalid: ", externalEvent);
            return false;
        }
        if (CollectionUtil.checkLoop(records.getList(), new ActionRecord(externalEvent, false, cache.getSnapShootMD5()))) {
            Logger.getDefault().ii("skip action to avoid loop: ", externalEvent);
            return false;
        }
        return actionGuard == null || actionGuard.isValid(externalEvent);
    }

    public <V> V obtainValue(Obtainable<V> obtainable, V expected) {
        return executor.sync(() -> {
            Property<V> property = obtainable.getProperty();
            if (property.isValueFresh()) {
                return property.getCurrentValue();
            }
            if (communicator != null && getTagCount() == 0) {
                V value = communicator.fetchState(obtainable);
                property.setStateSpaceFrameMark(frameMark);
                return value;
            }
            property.setStateSpaceFrameMark(frameMark);
            return expected;
        });
    }

    boolean verifyExpectation(NonPropertyExpectation expectation) {
        if (communicator != null && getTagCount() == 0) {
            return communicator.verifyExpectation(expectation);
        }
        return true;
    }

    void enterMethod(Object msg) {
        Logger.getDefault().concat(1, LogLevel.Verbose, '+', methodStack.incrementAndGet(), ':', msg);
    }

    void exitMethod(LogLevel logLevel, Object msg) {
        int depth = methodStack.getAndDecrement();
        Logger.getDefault().concat(1, logLevel, '-', depth, ':', ObjectUtil.getPresentation(msg));
    }
}
