package com.yanry.driver.core.model.expectation;

import com.yanry.driver.core.model.base.Expectation;

public abstract class ActionExpectation extends Expectation {

    public ActionExpectation() {
        super(Timing.IMMEDIATELY, false);
    }

    protected abstract void run();

    @Override
    protected final boolean doVerify() {
        run();
        return true;
    }
}
