package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.property.Property;

import java.util.function.BiPredicate;

/**
 * A key-value pair (aka state) expectation
 * Created by rongyu.yan on 5/10/2017.
 */
public abstract class PropertyExpectation<V> extends Expectation {

    public PropertyExpectation(Timing timing, boolean needCheck) {
        super(timing, needCheck);
    }

    public boolean isSatisfied(BiPredicate<Property<V>, V> predicate) {
        return predicate.test(getProperty(), getExpectedValue());
    }

    public abstract Property<V> getProperty();

    public abstract V getExpectedValue();

    @Override
    protected final boolean selfVerify(boolean verifySuperPaths) {
        V expectedValue = getExpectedValue();
        Property<V> property = getProperty();
        V oldValue = property.getCurrentValue();
        property.handleExpectation(expectedValue, isNeedCheck());
        if (expectedValue.equals(property.getCurrentValue())) {
            if (verifySuperPaths) {
                property.getGraph().verifySuperPaths(property, oldValue, expectedValue);
            }
            return true;
        }
        return false;
    }

    @Override
    protected final int getMatchDegree(Path path) {
        Property<V> property = getProperty();
        Object value = path.get(property);
        return value != null && !value.equals(property.getCurrentValue()) && value.equals(getExpectedValue()) ? 100 : 0;
    }
}
