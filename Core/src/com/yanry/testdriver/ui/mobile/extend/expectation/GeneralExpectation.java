package com.yanry.testdriver.ui.mobile.extend.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.expectation.StatelessExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.TransientExpectation;

/**
 * Created by rongyu.yan on 3/3/2017.
 */
@Presentable
public class GeneralExpectation extends TransientExpectation {
    private String desc;
    private Graph graph;

    public GeneralExpectation(Graph graph, String desc, Timing timing, int duration) {
        super(timing, duration);
        this.desc = desc;
        this.graph = graph;
    }

    @Presentable
    public String getDesc() {
        return desc;
    }

    @Override
    protected Graph getGraph() {
        return graph;
    }
}
