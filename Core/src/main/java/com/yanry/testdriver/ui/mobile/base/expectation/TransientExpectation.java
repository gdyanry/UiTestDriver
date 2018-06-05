package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;

/**
 * Created by rongyu.yan on 4/24/2017.
 */
public class TransientExpectation extends NonPropertyExpectation {
    private int duration;
    private Graph graph;

    public TransientExpectation(Timing timing, int duration, Graph graph) {
        super(timing);
        this.duration = duration;
        this.graph = graph;
    }

    @Presentable
    public int getDuration() {
        return duration;
    }

    @Override
    protected Graph getGraph() {
        return graph;
    }
}
