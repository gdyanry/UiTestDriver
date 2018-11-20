package com.yanry.driver.core.model.expectation;

import com.yanry.driver.core.model.base.Property;
import lib.common.util.object.EqualsPart;

import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public class SDPropertyExpectation<V> extends StaticPropertyExpectation<V> {
    private Supplier<V> valueSupplier;

    public SDPropertyExpectation(Timing timing, boolean needCheck, Property<V> property, Supplier<V> valueSupplier) {
        super(timing, needCheck, property);
        this.valueSupplier = valueSupplier;
    }

    @Override
    public V getExpectedValue() {
        return valueSupplier.get();
    }

    @EqualsPart
    public Supplier<V> getValueSupplier() {
        return valueSupplier;
    }
}
