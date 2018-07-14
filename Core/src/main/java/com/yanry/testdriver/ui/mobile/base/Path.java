/**
 *
 */
package com.yanry.testdriver.ui.mobile.base;

import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.event.StateEvent;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.property.Property;
import lib.common.model.Singletons;

import java.util.HashMap;
import java.util.Random;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
@Presentable
public class Path extends HashMap<Property, Object> {
    private Event event;
    private Expectation expectation;
    private int hashCode;
    private long timeFrame;
    private int unsatisfiedDegree;

    public Path(Event event, Expectation expectation) {
        this.event = event;
        this.expectation = expectation;
        hashCode = Singletons.get(Random.class).nextInt();
    }

    public <V> Path addInitState(Property<V> property, V value) {
        put(property, value);
        return this;
    }

    public int getUnsatisfiedDegree(long timeFrame, boolean isToRoll) {
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
            unsatisfiedDegree = keySet().stream().filter(property -> !property.equals(finalExcludeProperty) && !get(property).equals(property.getCurrentValue())).mapToInt(prop -> 1).sum();
            this.timeFrame = timeFrame;
        }
        return unsatisfiedDegree + (addOne ? 1 : 0);
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
