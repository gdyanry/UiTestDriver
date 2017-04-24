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
public class Toast extends TransientExpectation {
    private String message;
    private Graph graph;

    public Toast(Graph graph, String message, Timing timing, int duration) {
        super(timing, duration);
        this.message = message;
        this.graph = graph;
    }

    @Presentable
    public String getMessage() {
        return message;
    }

    @Override
    protected Graph getGraph() {
        return graph;
    }
}
