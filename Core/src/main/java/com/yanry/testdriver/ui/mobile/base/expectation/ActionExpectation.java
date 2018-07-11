package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;

public abstract class ActionExpectation extends Expectation {

    public ActionExpectation() {
        super(Timing.IMMEDIATELY, false);
    }

    protected abstract void run();

    @Override
    protected void onVerify() {

    }

    @Override
    protected final boolean doVerify(boolean verifySuperPaths) {
        run();
        return true;
    }

    @Override
    protected int getMatchDegree(Path path) {
        return 0;
    }
}
