package com.yanry.driver.core.model.expectation;

import com.yanry.driver.core.model.Path;

public abstract class ActionExpectation extends Expectation {

    public ActionExpectation() {
        super(Timing.IMMEDIATELY, false);
    }

    protected abstract void run();

    @Override
    protected void onVerify() {

    }

    @Override
    protected final boolean doVerify() {
        run();
        return true;
    }

    @Override
    protected int getMatchDegree(Path path) {
        return 0;
    }
}