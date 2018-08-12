/**
 *
 */
package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.event.StateEvent;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.core.model.state.StateEquals;
import com.yanry.driver.core.model.state.StatePredicate;

import java.util.HashMap;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
@Presentable
public class Path {
    private Event event;
    private Expectation expectation;
    private long timeFrame;
    private int unsatisfiedDegree;
    HashMap<Property, StatePredicate> initState;

    public Path(Event event, Expectation expectation) {
        this.event = event;
        this.expectation = expectation;
        initState = new HashMap<>();
    }

    public <V> Path addInitState(Property<V> property, V value) {
        initState.put(property, new StateEquals(value));
        return this;
    }

    public <V> Path addInitStatePredicate(Property<V> property, StatePredicate<V> predicate) {
        initState.put(property, predicate);
        return this;
    }

    int getUnsatisfiedDegree(long timeFrame, boolean isToRoll) {
        Property excludeProperty = null;
        boolean addOne = false;
        if (event instanceof StateEvent) {
            StateEvent stateEvent = (StateEvent) event;
            if (isToRoll) {
                Object from = stateEvent.getFrom();
                if (from != null && !from.equals(stateEvent.getProperty().getCurrentValue())) {
                    addOne = true;
                }
            } else {
                excludeProperty = stateEvent.getProperty();
            }
        }
        if (timeFrame == 0 || timeFrame != this.timeFrame) {
            Property finalExcludeProperty = excludeProperty;
            unsatisfiedDegree = initState.keySet().stream()
                    .filter(property -> !property.equals(finalExcludeProperty) && !initState.get(property).test(property.getCurrentValue()))
                    .mapToInt(prop -> 1).sum();
            this.timeFrame = timeFrame;
        }
        return unsatisfiedDegree + (addOne ? 1 : 0);
    }

    @Presentable
    public HashMap<Property, StatePredicate> getInitState() {
        return initState;
    }

    @Presentable
    public Event getEvent() {
        return event;
    }

    @Presentable
    public Expectation getExpectation() {
        return expectation;
    }
}
