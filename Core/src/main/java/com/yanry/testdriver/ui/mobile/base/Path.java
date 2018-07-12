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
        if (timeFrame == 0 || timeFrame != this.timeFrame) {
            unsatisfiedDegree = entrySet().stream().filter(state -> !state.getValue().equals(state.getKey().getCurrentValue())).mapToInt(state -> 1).sum();
            this.timeFrame = timeFrame;
        }
        return unsatisfiedDegree + (isToRoll ? getUnsatisfiedDegreeByEvent() : 0);
    }

    private int getUnsatisfiedDegreeByEvent() {
        if (event instanceof StateEvent) {
            StateEvent stateEvent = (StateEvent) event;
            Object from = stateEvent.getFrom();
            if (from != null && !from.equals(stateEvent.getProperty().getCurrentValue())) {
                return 1;
            }
        }
        return 0;
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
