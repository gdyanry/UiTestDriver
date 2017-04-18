/**
 *
 */
package com.yanry.testdriver.ui.mobile.model.base;

import com.yanry.testdriver.ui.mobile.model.base.process.ProcessState;
import com.yanry.testdriver.ui.mobile.model.base.process.StartProcess;
import com.yanry.testdriver.ui.mobile.model.base.process.StopProcess;
import com.yanry.testdriver.ui.mobile.model.base.window.Visibility;
import com.yanry.testdriver.ui.mobile.model.base.window.Window;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author yanry
 *         <p>
 *         Jan 5, 2017
 */
public class Graph implements Communicator {
    private List<Path> allPaths;
    private Set<Path> unprocessedPaths;
    private Set<Path> failedTransitionPaths;
    private List<Object> testRecords;
    private boolean isTraversing;
    private Set<Path> rollingPaths;
    private List<Communicator> communicators;
    private ProcessState processState;
    private Set<Path> retainedPaths;
    private List<Path> optionalPaths;

    public Graph() {
        allPaths = new LinkedList<>();
        testRecords = new LinkedList<>();
        failedTransitionPaths = new HashSet<>();
        rollingPaths = new HashSet<>();
        retainedPaths = new HashSet<>();
        unprocessedPaths = new HashSet<>();
        communicators = new ArrayList<>();
        processState = new ProcessState();
        new Path(this, null, new StartProcess(), new PermanentExpectation<>(processState, true, Timing.IMMEDIATELY))
                .put(processState, false);
        new Path(this, null, new StopProcess(), new PermanentExpectation<>(processState, false, Timing.IMMEDIATELY));
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

    public List<Path> prepare() {
        if (optionalPaths == null) {
            synchronized (this) {
                if (optionalPaths == null) {
                    optionalPaths = allPaths.stream().filter(p -> {
                        p.preProcess();
                        if (p.getExpectation() instanceof PermanentExpectation) {
                            retainedPaths.add(p);
                            PermanentExpectation endState = (PermanentExpectation) p.getExpectation();
                            if (!endState.getProperty().isNeedVerification()) {
                                return false;
                            }
                        } else if (p.getExpectation() instanceof FollowingAction) {
                            retainedPaths.add(p);
                            return false;
                        }
                        return true;
                    }).sorted(Comparator.comparingInt(p -> p.getWindow() == null ? 0 : p.getWindow().hashCode()))
                            .collect(Collectors.toList());
                }
            }
        }
        return optionalPaths;
    }

    /**
     * @param indexesOfOptionalPath null indicates traversing all paths.
     * @return return null if traversing is currently processing.
     */
    public List<Object> traverse(int[] indexesOfOptionalPath) {
        if (isTraversing) {
            return null;
        }
        isTraversing = true;
        if (optionalPaths == null) {
            prepare();
        }
        testRecords.clear();
        failedTransitionPaths.clear();
        unprocessedPaths.clear();
        if (indexesOfOptionalPath == null) {
            unprocessedPaths.addAll(optionalPaths);
        } else {
            for (int index : indexesOfOptionalPath) {
                unprocessedPaths.add(optionalPaths.get(index));
            }
        }
        while (!unprocessedPaths.isEmpty() && isTraversing) {
            rollingPaths.clear();
            roll(unprocessedPaths.stream().findAny().get(), false);
        }
        List<Object> result = new ArrayList<>(testRecords);
        isTraversing = false;
        return result;
    }

    /**
     * This should be called from a different thread.
     */
    public void abort() {
        isTraversing = false;
    }

    private boolean roll(Path path, boolean isTransitEvent) {
        if (!rollingPaths.add(path)) {
            return false;
        }
        return internalRoll(path, isTransitEvent);
    }

    private boolean internalRoll(Path path, boolean isTransitEvent) {
        // make sure environment states are satisfied.
        Optional<Map.Entry<ObjectProperty, Object>> any = path.entrySet().stream().filter(state -> !state.getKey().
                getCurrentValue().equals(state.getValue())).findAny();
        if (any.isPresent()) {
            Map.Entry<ObjectProperty, Object> state = any.get();
            if (state.getKey().transitTo(this, state.getValue(), false)) {
                return internalRoll(path, isTransitEvent);
            } else {
                if (unprocessedPaths.remove(path)) {
                    testRecords.add(new MissedPath(path, state.getKey()));
                }
                return false;
            }
        }
        // jump to window
        Window window = path.getWindow();
        if (window != null && window.getState().getCurrentValue() != Visibility.Foreground) {
            // pop this window to foreground
            if (window.getState().transitTo(this, Visibility.Foreground, isTransitEvent)) {
                return internalRoll(path, isTransitEvent);
            }
            if (unprocessedPaths.remove(path)) {
                testRecords.add(new MissedPath(path, window.getState()));
            }
            return false;
        }
        // trigger event
        Event inputEvent = path.getEvent();
        if (inputEvent != null) {
            if (inputEvent instanceof StateTransitionEvent) {
                StateTransitionEvent event = (StateTransitionEvent) inputEvent;
                // roll path for this transition event.
                if (!transitToState(event.getProperty(), event.getFrom(), false) ||
                        !event.getProperty().transitTo(this, event.getTo(), true)) {
                    if (unprocessedPaths.remove(path)) {
                        testRecords.add(new MissedPath(path, event.getProperty()));
                    }
                    return false;
                }
            } else if (inputEvent instanceof ActionEvent) {
                ActionEvent event = (ActionEvent) inputEvent;
                if (performAction(event)) {
                    testRecords.add(event);
                } else {
                    if (unprocessedPaths.remove(path)) {
                        testRecords.add(new MissedPath(path, event));
                    }
                    return false;
                }
            }
        }
        // collect paths that share the same environment states and event
        final boolean[] result = new boolean[1];
        allPaths.stream().filter(p -> p == path || (retainedPaths.contains(p) || unprocessedPaths.contains(p)) &&
                p.getEvent() == inputEvent && p.isSatisfied())
                .sorted(Comparator.comparingInt(p -> allPaths.indexOf(p))).forEach(p -> {
            if (p == path) {
                result[0] = verifyPath(p, isTransitEvent);
            } else {
                verifyPath(p, false);
            }
        });
        return result[0];
    }

    private boolean verifyPath(Path path, boolean isTransitEvent) {
        unprocessedPaths.remove(path);
        rollingPaths.remove(path);
        Expectation expectation = path.getExpectation();
        boolean isPass;
        if (expectation instanceof PermanentExpectation) {
            PermanentExpectation endState = (PermanentExpectation) expectation;
            List<Path> postPaths = null;
            if (!isTransitEvent) {
                // see if this becomes state transition event of other paths
                postPaths = allPaths.stream().filter(p -> {
                    if ((retainedPaths.contains(p) || unprocessedPaths.contains(p))
                            && !rollingPaths.contains(p) && p.getEvent() instanceof StateTransitionEvent) {
                        StateTransitionEvent transitionEvent = (StateTransitionEvent) p.getEvent();
                        if (transitionEvent.getProperty() == endState.getProperty() && transitionEvent.getTo() == endState.getValue()
                                && transitionEvent.getFrom().test(endState.getProperty().getCurrentValue())
                                && p.isSatisfied()) {
                            return true;
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
            }
            if (endState.getProperty().isNeedVerification()) {
                isPass = endState.getValue().equals(endState.getProperty().checkValue(endState.getTiming()));
                testRecords.add(new Assertion(endState, isPass));
                if (!isPass) {
                    failedTransitionPaths.add(path);
                }
            } else {
                isPass = true;
                // handle verification free situation
                endState.getProperty().setCacheValue(endState.getValue());
            }
            if (isPass && postPaths != null) {
                for (Path p : postPaths) {
                    verifyPath(p, false);
                }
            }
        } else if (expectation instanceof FollowingAction) {
            isPass = true;
            FollowingAction followingAction = (FollowingAction) expectation;
            followingAction.run();
        } else {
            isPass = verifyExpectation((TransientExpectation) expectation);
            testRecords.add(new Assertion(expectation, isPass));
        }
        if (isPass && !path.getFollowingActions().isEmpty()) {
            for (FollowingAction followingAction : path.getFollowingActions()) {
                followingAction.run();
            }
        }
        return isPass;
    }

    public void verifyPathsByTransitionEvent(StateTransitionEvent event, Supplier<Boolean> doTransit) {
        List<Path> paths = allPaths.stream().filter(p -> (retainedPaths.contains(p) || unprocessedPaths.contains(p))
                && !rollingPaths.contains(p) && p.getEvent().equals(event) && p.isSatisfied()).collect(Collectors.toList());
        if (doTransit.get()) {
            for (Path path : paths) {
                verifyPath(path, false);
            }
        }
    }

    public <V> boolean transitToState(ObjectProperty<V> property, Predicate<V> to, boolean isTransitEvent) {
        if (to.test(property.getCurrentValue())) {
            return true;
        }
        return findPathToRoll(isTransitEvent, (p, pe) -> pe.getProperty().equals(property) && to.test((V) pe
                .getValue()));
    }

    public boolean findPathToRoll(boolean isTransitEvent, BiPredicate<Path, PermanentExpectation> judge) {
        return retainedPaths.stream().filter(p -> {
            if (failedTransitionPaths.contains(p) || !(p.getExpectation() instanceof PermanentExpectation)) {
                return false;
            }
            PermanentExpectation expectation = (PermanentExpectation) p.getExpectation();
            return judge.test(p, expectation);
        }).sorted(Comparator.comparingInt(p -> (p.isSatisfied() ? 0 : 1))).anyMatch(p -> roll(p, isTransitEvent));
    }

    @Override
    public <V> V checkState(StateToCheck<V> stateToCheck) {
        for (Communicator communicator : communicators) {
            V value = communicator.checkState(stateToCheck);
            if (value != null) {
                return value;
            }
        }
        return null;
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
    public Boolean verifyExpectation(TransientExpectation expectation) {
        for (Communicator communicator : communicators) {
            Boolean result = communicator.verifyExpectation(expectation);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}