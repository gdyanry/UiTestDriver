/**
 *
 */
package com.yanry.testdriver.ui.mobile.base;

import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.event.ValueSwitchEvent;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.property.Property;
import lib.common.model.Singletons;

import java.util.HashMap;
import java.util.Random;

/**
 * @author yanry
 *         <p>
 *         Jan 5, 2017
 */
@Presentable
public class Path extends HashMap<Property, Object> {
    private Event event;
    private Expectation expectation;
    private int hashCode;

    public Path(Event event, Expectation expectation) {
        this.event = event;
        this.expectation = expectation;
        hashCode = Singletons.get(Random.class).nextInt();
    }

    public <V> Path addInitState(Property<V> property, V value) {
        put(property, value);
        return this;
    }

    public void preProcess() {
        if (event instanceof ValueSwitchEvent) {
            ValueSwitchEvent transitionEvent = (ValueSwitchEvent) event;
            Property property = transitionEvent.getProperty();
            remove(property);
        }
    }

    public boolean isSatisfied() {
        return entrySet().stream().allMatch(state -> state.getValue().equals(state.getKey().getCurrentValue()));
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
