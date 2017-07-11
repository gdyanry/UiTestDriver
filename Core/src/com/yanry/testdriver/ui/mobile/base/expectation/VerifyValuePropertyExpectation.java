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
public class VerifyValuePropertyExpectation<V> extends PropertyExpectation<V, Property<V>> {
    public VerifyValuePropertyExpectation(Timing timing, Property<V> property, V value) {
        super(timing, property, value);
    }

    public VerifyValuePropertyExpectation(Timing timing, Property<V> property, Supplier<V> valueSupplier) {
        super(timing, property, valueSupplier);
    }

    @Override
    public boolean ifRecord() {
        return true;
    }

    @Override
    protected boolean isSelfSatisfied(BiPredicate<SwitchBySearchProperty, Object> endStatePredicate) {
        return false;
    }

    @Override
    protected boolean doSelfVerify(List<Path> superPathContainer) {
        return getValue().equals(getProperty().getCurrentValue());
    }
}
