package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.property.Property;

import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public class DynamicPropertyExpectation<V> extends PropertyExpectation<V> {
    private Supplier<V> valueSupplier;

    public DynamicPropertyExpectation(Timing timing, boolean needCheck, Property<V> property, Supplier<V> valueSupplier) {
        super(timing, needCheck, property);
        this.valueSupplier = valueSupplier;
    }

    @Override
    public V getExpectedValue() {
        return valueSupplier.get();
    }
}
