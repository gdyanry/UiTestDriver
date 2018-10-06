package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.base.Expectation;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import lib.common.util.object.Presentable;

/**
 * This class represents a transient expectation such as a toast or a loading dialog.
 * Created by rongyu.yan on 3/9/2017.
 */
@Presentable
public abstract class NonPropertyExpectation extends Expectation {
    private Graph graph;

    public NonPropertyExpectation(Timing timing, Graph graph) {
        super(timing, true);
        this.graph = graph;
    }

    @Override
    protected void onVerify() {

    }

    @Override
    protected final boolean doVerify() {
        return graph.verifyExpectation(this);
    }
}
