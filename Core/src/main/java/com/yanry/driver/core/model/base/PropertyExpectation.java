package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.expectation.Timing;
import yanry.lib.java.model.log.Logger;

import java.util.Objects;

/**
 * A key-value pair (aka state) expectation
 * Created by rongyu.yan on 5/10/2017.
 */
public abstract class PropertyExpectation<V> extends Expectation {

    public PropertyExpectation(Timing timing, boolean needCheck) {
        super(timing, needCheck);
    }

    public abstract V getExpectedValue();

    public abstract Property<V> getProperty();

    @Override
    protected final boolean doVerify() {
        V expectedValue = getExpectedValue();
        Property<V> property = getProperty();
        property.handleExpectation(expectedValue, isNeedCheck());
        V actualValue = property.getCurrentValue(expectedValue);
        if (Objects.equals(expectedValue, actualValue)) {
            return true;
        } else {
            Logger.getDefault().ww("expected value: ", expectedValue, ", actual value:  ", actualValue);
            return false;
        }
    }
}
