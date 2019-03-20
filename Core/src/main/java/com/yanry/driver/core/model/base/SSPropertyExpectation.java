package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.expectation.StaticPropertyExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.Visible;

public class SSPropertyExpectation<V> extends StaticPropertyExpectation<V> {
    private V value;

    public SSPropertyExpectation(Timing timing, boolean needCheck, Property<V> property, V value) {
        super(timing, needCheck, property);
        this.value = value;
        property.addValue(value);
    }

    @EqualsPart
    @Visible
    @Override
    public V getExpectedValue() {
        return value;
    }
}
