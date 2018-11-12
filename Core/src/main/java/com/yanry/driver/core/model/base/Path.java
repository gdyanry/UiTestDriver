/**
 *
 */
package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.predicate.Equals;
import lib.common.util.object.Visible;
import lib.common.util.object.VisibleObject;

import java.util.HashMap;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
public class Path extends VisibleObject {
    private static final int UNSATISFIED_DEGREE_STEP = 10;
    private Event event;
    private Expectation expectation;
    private long timeFrame;
    private int unsatisfiedDegree;
    private int baseUnsatisfiedDegree;
    private HashMap<Property, ValuePredicate> context;

    Path(Event event, Expectation expectation) {
        this.event = event;
        this.expectation = expectation;
        context = new HashMap<>();
    }

    public <V> Path addContextState(Property<V> property, V value) {
        context.put(property, new Equals(value));
        return this;
    }

    public <V> Path addContextStatePredicate(Property<V> property, ValuePredicate<V> predicate) {
        context.put(property, predicate);
        property.findValueToAdd(predicate);
        return this;
    }

    public Path setBaseUnsatisfiedDegree(int baseUnsatisfiedDegree) {
        this.baseUnsatisfiedDegree = baseUnsatisfiedDegree;
        return this;
    }

    int getUnsatisfiedDegree(long timeFrame, boolean isToRoll) {
        Property excludeProperty = null;
        boolean addOneStep = false;
        if (event instanceof TransitionEvent) {
            TransitionEvent transitionEvent = (TransitionEvent) event;
            if (isToRoll) {
                ValuePredicate from = transitionEvent.getFrom();
                if (!from.test(transitionEvent.getProperty().getCurrentValue())) {
                    addOneStep = true;
                }
            } else {
                excludeProperty = transitionEvent.getProperty();
            }
        }
        if (timeFrame == 0 || timeFrame != this.timeFrame) {
            Property finalExcludeProperty = excludeProperty;
            unsatisfiedDegree = context.keySet().stream()
                    .filter(property -> !property.equals(finalExcludeProperty) && !context.get(property).test(property.getCurrentValue()))
                    .mapToInt(prop -> UNSATISFIED_DEGREE_STEP).sum();
            this.timeFrame = timeFrame;
        }
        int result = unsatisfiedDegree + (addOneStep ? UNSATISFIED_DEGREE_STEP : 0);
        if (result > 0) {
            result += baseUnsatisfiedDegree;
        }
        return result;
    }

    @Visible
    public HashMap<Property, ValuePredicate> getContext() {
        return context;
    }

    @Visible
    public Event getEvent() {
        return event;
    }

    @Visible
    public Expectation getExpectation() {
        return expectation;
    }
}
