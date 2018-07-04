package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;

/**
 * This class represents a transient expectation such as a toast or a loading dialog.
 * Created by rongyu.yan on 3/9/2017.
 */
@Presentable
public abstract class NonPropertyExpectation extends Expectation {
    private Graph graph;

    public NonPropertyExpectation(Timing timing, boolean needCheck, Graph graph) {
        super(timing, needCheck);
        this.graph = graph;
    }

    public NonPropertyExpectation(Timing timing) {
        super(timing, true);
    }

    @Override
    protected final boolean selfVerify(boolean verifySuperPaths) {
        return graph.verifyExpectation(this);
    }

    @Override
    protected int getMatchDegree(Path path) {
        return 0;
    }
}
