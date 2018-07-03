package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;

public abstract class ActionExpectation extends Expectation {

    public ActionExpectation() {
        super(Timing.IMMEDIATELY, false);
    }

    protected abstract void run(Graph graph);

    @Override
    protected final boolean selfVerify(Graph graph, boolean verifySuperPaths) {
        run(graph);
        return true;
    }

    @Override
    protected int getMatchDegree(Graph graph, Path path) {
        return 0;
    }
}
