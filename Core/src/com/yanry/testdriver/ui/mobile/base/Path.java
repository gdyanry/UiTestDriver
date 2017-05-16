/**
 *
 */
package com.yanry.testdriver.ui.mobile.base;

import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.event.StateSwitchEvent;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.property.SwitchableProperty;
import lib.common.model.Singletons;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author yanry
 *         <p>
 *         Jan 5, 2017
 */
@Presentable
public class Path extends HashMap<SwitchableProperty, Object> {
    private Event event;
    private Expectation expectation;
    private Set<Consumer<List<Path>>> followingActions;
    private int hashCode;

    public Path(Event event, Expectation expectation) {
        this.event = event;
        this.expectation = expectation;
        followingActions = new HashSet<>();
        hashCode = Singletons.get(Random.class).nextInt();
    }

    public <V> Path addInitState(SwitchableProperty<V> property, V value) {
        put(property, value);
        return this;
    }

    public Path addFollowingAction(Consumer<List<Path>> action) {
        followingActions.add(action);
        return this;
    }

    public void preProcess() {
        if (event instanceof StateSwitchEvent) {
            StateSwitchEvent transitionEvent = (StateSwitchEvent) event;
            SwitchableProperty property = transitionEvent.getProperty();
            remove(property);
        }
    }

    public boolean isSatisfied() {
        return entrySet().stream().allMatch(state -> state.getValue().equals(state.getKey().getCurrentValue()));
    }

    public Set<Consumer<List<Path>>> getFollowingActions() {
        return followingActions;
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
