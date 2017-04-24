package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;

import java.util.List;

/**
 * Created by rongyu.yan on 3/3/2017.
 */
public abstract class DynamicExpectation implements Runnable, Expectation {
    @Override
    public boolean verify(List<Path> superPathContainer) {
        run();
        return true;
    }

    @Override
    public boolean ifRecord() {
        return false;
    }
}
