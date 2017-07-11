package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.property.SwitchBySearchProperty;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * A dynamic action expectation.
 * Created by rongyu.yan on 3/3/2017.
 */
public abstract class ActionExpectation implements Expectation {

    protected abstract void run(List<Path> superPathContainer);

    @Override
    public boolean verify(List<Path> superPathContainer) {
        run(superPathContainer);
        return true;
    }

    @Override
    public boolean isSatisfied(BiPredicate<SwitchBySearchProperty, Object> endStatePredicate) {
        return false;
    }

    @Override
    public boolean ifRecord() {
        return false;
    }
}
