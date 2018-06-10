package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.property.Property;

import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public class DynamicPropertyExpectation<V> extends PropertyExpectation<V> {
    private Supplier<V> valueSupplier;

    public DynamicPropertyExpectation(Timing timing, Property<V> property, Supplier<V> valueSupplier) {
        super(timing, property);
        this.valueSupplier = valueSupplier;
    }

    @Override
    protected final boolean selfVerify(Graph graph) {
        return getProperty().getCurrentValue(graph).equals(valueSupplier.get());
    }

    @Override
    public final boolean ifRecord() {
        return true;
    }
}
