package com.yanry.driver.core.model.expectation;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.PropertyExpectation;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.function.Supplier;

public class DDPropertyExpectation<V> extends PropertyExpectation<V> {
    private Supplier<Property<V>> propertySupplier;
    private Supplier<V> valueSupplier;

    public DDPropertyExpectation(Timing timing, boolean needCheck, Supplier<Property<V>> propertySupplier, Supplier<V> valueSupplier) {
        super(timing, needCheck);
        this.propertySupplier = propertySupplier;
        this.valueSupplier = valueSupplier;
    }

    @EqualsPart
    @Visible
    @Override
    public Property<V> getProperty() {
        return propertySupplier.get();
    }

    @Override
    protected V doGetExpectedValue() {
        return valueSupplier.get();
    }
}
