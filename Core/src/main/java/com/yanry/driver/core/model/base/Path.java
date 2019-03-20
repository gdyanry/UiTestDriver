/**
 *
 */
package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.predicate.Equals;
import yanry.lib.java.util.object.Visible;
import yanry.lib.java.util.object.VisibleObject;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
public class Path extends VisibleObject {
    private static final int UNSATISFIED_DEGREE_STEP_LENGTH = 10;
    private Event event;
    private Expectation expectation;
    private int baseUnsatisfiedDegree;
    private Context context;

    Path(Event event, Expectation expectation) {
        this.event = event;
        this.expectation = expectation;
        context = new Context();
    }

    public <V> Path addContextValue(Property<V> property, V value) {
        context.add(property, Equals.of(value));
        return this;
    }

    public <V> Path addContextPredicate(Property<V> property, ValuePredicate<V> predicate) {
        context.add(property, predicate);
        return this;
    }

    public Path setBaseUnsatisfiedDegree(int baseUnsatisfiedDegree) {
        this.baseUnsatisfiedDegree = baseUnsatisfiedDegree;
        return this;
    }

    int getUnsatisfiedDegree(long frameMark, boolean isToRoll) {
        Property excludeProperty = null;
        int result = 0;
        if (event instanceof TransitionEvent) {
            TransitionEvent transitionEvent = (TransitionEvent) event;
            if (isToRoll) {
                ValuePredicate from = transitionEvent.getFrom();
                if (!from.test(transitionEvent.getProperty().getCurrentValue())) {
                    result = UNSATISFIED_DEGREE_STEP_LENGTH;
                }
            } else {
                excludeProperty = transitionEvent.getProperty();
            }
        } else if (event instanceof ExternalEvent) {
            ExternalEvent externalEvent = (ExternalEvent) event;
            Context precondition = externalEvent.getPrecondition();
            if (precondition != null) {
                result = precondition.getUnsatisfiedDegree(frameMark, null, UNSATISFIED_DEGREE_STEP_LENGTH);
            }
        }
        result += context.getUnsatisfiedDegree(frameMark, excludeProperty, UNSATISFIED_DEGREE_STEP_LENGTH);
        if (result > 0) {
            result += baseUnsatisfiedDegree;
        }
        return result;
    }

    @Visible
    public Context getContext() {
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
