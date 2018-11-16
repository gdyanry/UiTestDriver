package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.expectation.Timing;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.Objects;

/**
 * A key-value pair (aka state) expectation
 * Created by rongyu.yan on 5/10/2017.
 */
public abstract class PropertyExpectation<V> extends Expectation {

    public PropertyExpectation(Timing timing, boolean needCheck) {
        super(timing, needCheck);
    }

    @Visible
    @EqualsPart
    public final V getExpectedValue() {
        try {
            return doGetExpectedValue();
        } catch (Exception e) {
            return null;
        }
    }

    public abstract Property<V> getProperty();

    protected abstract V doGetExpectedValue();

    @Override
    protected final boolean doVerify() {
        V expectedValue = getExpectedValue();
        Property<V> property = getProperty();
        V oldValue = property.getCurrentValue();
        property.handleExpectation(expectedValue, isNeedCheck());
        V actualValue = property.getCurrentValue();
        if (!Objects.equals(actualValue, oldValue)) {
            property.getGraph().verifySuperPaths(property, oldValue, actualValue);
        }
        return Objects.equals(expectedValue, actualValue);
    }
}
