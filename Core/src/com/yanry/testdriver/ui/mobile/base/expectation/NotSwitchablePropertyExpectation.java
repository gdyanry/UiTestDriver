package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.property.Property;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 5/10/2017.
 */
public abstract class NotSwitchablePropertyExpectation<V> extends PropertyExpectation<V, Property> {

    public NotSwitchablePropertyExpectation(Timing timing, Property property, V value) {
        super(timing, property, value);
    }

    public NotSwitchablePropertyExpectation(Timing timing, Property property, Supplier<V> valueSupplier) {
        super(timing, property, valueSupplier);
    }

    protected abstract Graph getGraph();

    @Override
    public boolean ifRecord() {
        return true;
    }

    @Override
    protected boolean doVerify(List<Path> superPathContainer) {
        return getGraph().verifyExpectation(this);
    }
}
