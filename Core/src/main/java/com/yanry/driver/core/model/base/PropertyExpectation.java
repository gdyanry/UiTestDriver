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
}
