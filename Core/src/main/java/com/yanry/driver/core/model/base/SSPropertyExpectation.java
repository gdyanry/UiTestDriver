package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.expectation.StaticPropertyExpectation;
import com.yanry.driver.core.model.expectation.Timing;

public class SSPropertyExpectation<V> extends StaticPropertyExpectation<V> {
    private V value;

    public SSPropertyExpectation(Timing timing, boolean needCheck, Property<V> property, V value) {
        super(timing, needCheck, property);
        this.value = value;
        property.addValue(value);
    }

    @Override
    protected V doGetExpectedValue() {
        return value;
    }
}
