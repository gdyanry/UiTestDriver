package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.runtime.Presentable;

/**
 * A key-value pair (aka state) expectation
 * Created by rongyu.yan on 5/10/2017.
 */
public abstract class PropertyExpectation<V> extends Expectation {
    private V oldValue;

    public PropertyExpectation(Timing timing, boolean needCheck) {
        super(timing, needCheck);
    }

    @Presentable
    public final V getExpectedValue() {
        try {
            return doGetExpectedValue();
        } catch (Exception e) {
            return null;
        }
    }

    protected abstract V doGetExpectedValue();

    public abstract Property<V> getProperty();

    @Override
    protected final void onVerify() {
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
        return expectedValue != null && expectedValue.equals(actualValue);
    }
}
