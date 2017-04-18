/**
 *
 */
package com.yanry.testdriver.ui.mobile.model.base;

import com.yanry.testdriver.ui.mobile.model.base.process.ProcessState;
import com.yanry.testdriver.ui.mobile.model.base.window.Visibility;
import com.yanry.testdriver.ui.mobile.model.base.window.Window;
import lib.common.model.Singletons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author yanry
 *         <p>
 *         Jan 5, 2017
 */
@Presentable
public class Path extends HashMap<ObjectProperty, Object> {
    private Window window;
    private Event event;
    private Expectation expectation;
    private Set<FollowingAction> followingActions;
    private int hashCode;

    public Path(Graph graph, Window window, Event event, Expectation expectation) {
        this.window = window;
        this.event = event;
        this.expectation = expectation;
        followingActions = new HashSet<>();
        hashCode = Singletons.get(Random.class).nextInt();
        put(graph.getProcessState(), true);
        graph.addPath(this);
    }

    public Path addFollowingAction(FollowingAction action) {
        followingActions.add(action);
        return this;
    }

    public void preProcess() {
        if (event instanceof StateTransitionEvent) {
            StateTransitionEvent transitionEvent = (StateTransitionEvent) event;
            ObjectProperty property = transitionEvent.getProperty();
            remove(property);
            if (property instanceof ProcessState && transitionEvent.getTo().equals(true)) {
                window = null;
            }
        }
    }

    public boolean isSatisfied() {
        return (window == null || window.getState().getCurrentValue() == Visibility.Foreground) &&
                entrySet().stream().allMatch(state -> state.getKey().getCurrentValue().equals(state.getValue()));
    }

    public Set<FollowingAction> getFollowingActions() {
        return followingActions;
    }

    @Presentable
    public Window getWindow() {
        return window;
    }

    @Presentable
    public Event getEvent() {
        return event;
    }

    @Presentable
    public Expectation getExpectation() {
        return expectation;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
