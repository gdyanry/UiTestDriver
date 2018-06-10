package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;

public abstract class ActionExpectation extends Expectation {

    public ActionExpectation() {
        super(Timing.IMMEDIATELY);
    }

    protected abstract void run();

    @Override
    protected final boolean selfVerify(Graph graph) {
        run();
        return true;
    }

    @Override
    public final boolean ifRecord() {
        return false;
    }
}
