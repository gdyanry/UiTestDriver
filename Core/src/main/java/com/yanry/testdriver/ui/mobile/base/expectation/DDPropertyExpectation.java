package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.Property;

import java.util.function.Supplier;

public class DDPropertyExpectation<V> extends PropertyExpectation<V> {
    private Supplier<Property<V>> propertySupplier;
    private Supplier<V> valueSupplier;

    public DDPropertyExpectation(Timing timing, boolean needCheck, Supplier<Property<V>> propertySupplier, Supplier<V> valueSupplier) {
        super(timing, needCheck);
        this.propertySupplier = propertySupplier;
        this.valueSupplier = valueSupplier;
    }

    @Presentable
    @Override
    public Property<V> getProperty() {
        return propertySupplier.get();
    }

    @Presentable
    @Override
    public V getExpectedValue() {
        return valueSupplier.get();
    }
}
