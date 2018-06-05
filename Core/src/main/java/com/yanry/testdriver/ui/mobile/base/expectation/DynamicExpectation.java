package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.property.SwitchBySearchProperty;

import java.util.function.BiPredicate;

public abstract class DynamicExpectation extends Expectation {
    public DynamicExpectation() {
        super(Timing.IMMEDIATELY);
    }

    @Override
    protected boolean isSelfSatisfied(BiPredicate<SwitchBySearchProperty, Object> endStatePredicate) {
        return false;
    }

    @Override
    public boolean ifRecord() {
        return false;
    }
}