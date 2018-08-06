package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.event.StateEvent;
import com.yanry.driver.core.model.expectation.Timing;

import java.util.function.BiPredicate;

/**
 * A key-value pair (aka state) expectation
 * Created by rongyu.yan on 5/10/2017.
 */
public abstract class PropertyExpectation<V> extends Expectation {
    private V oldValue;

    public PropertyExpectation(Timing timing, boolean needCheck) {
        super(timing, needCheck);
    }

    public abstract Property<V> getProperty();

    public abstract V getExpectedValue();

    @Override
    protected void onVerify() {
        oldValue = getProperty().getCurrentValue();
    }

    @Override
    protected final boolean doVerify() {
        V expectedValue = getExpectedValue();
        Property<V> property = getProperty();
        property.handleExpectation(expectedValue, isNeedCheck());
        V actualValue = property.getCurrentValue();
        if (actualValue != null && !actualValue.equals(oldValue)) {
            property.getGraph().verifySuperPaths(property, oldValue, actualValue);
        }
        return expectedValue.equals(actualValue);
    }

    @Override
    protected final int getMatchDegree(Path path) {
        Property<V> property = getProperty();
        if (path.getEvent() instanceof StateEvent) {
            StateEvent stateEvent = (StateEvent) path.getEvent();
            if (stateEvent.getProperty().equals(property) && isMatch(property, stateEvent.getFrom())) {
                return 1;
            }
        }
        Object value = path.initState.get(property);
        return isMatch(property, value) ? 1 : 0;
    }

    private boolean isMatch(Property<V> property, Object value) {
        return value != null && !value.equals(property.getCurrentValue()) && value.equals(getExpectedValue());
    }
}
