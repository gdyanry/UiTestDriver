package com.yanry.driver.core.model.expectation;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.PropertyExpectation;
import yanry.lib.java.util.object.EqualsPart;

import java.util.function.Supplier;

public class DDPropertyExpectation<V> extends PropertyExpectation<V> {
    private Supplier<Property<V>> propertySupplier;
    private Supplier<V> valueSupplier;

    public DDPropertyExpectation(Timing timing, boolean needCheck, Supplier<Property<V>> propertySupplier, Supplier<V> valueSupplier) {
        super(timing, needCheck);
        this.propertySupplier = propertySupplier;
        this.valueSupplier = valueSupplier;
    }

    @Override
    public V getExpectedValue() {
        return valueSupplier.get();
    }

    @Override
    public Property<V> getProperty() {
        return propertySupplier.get();
    }

    @EqualsPart
    public Supplier<Property<V>> getPropertySupplier() {
        return propertySupplier;
    }

    @EqualsPart
    public Supplier<V> getValueSupplier() {
        return valueSupplier;
    }
}
