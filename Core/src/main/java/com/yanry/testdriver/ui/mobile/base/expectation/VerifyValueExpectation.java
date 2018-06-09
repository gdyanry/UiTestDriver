package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.property.Property;
import com.yanry.testdriver.ui.mobile.base.property.SwitchBySearchProperty;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public class VerifyValueExpectation<V> extends PropertyExpectation<V, Property<V>> {
    public VerifyValueExpectation(Timing timing, Property<V> property, V value) {
        super(timing, property, value);
    }

    public VerifyValueExpectation(Timing timing, Property<V> property, Supplier<V> valueSupplier) {
        super(timing, property, valueSupplier);
    }

    @Override
    public boolean ifRecord() {
        return true;
    }

    @Override
    protected boolean doSelfVerify() {
        return getValue().equals(getProperty().getCurrentValue());
    }
}
