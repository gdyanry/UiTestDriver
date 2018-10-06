package com.yanry.driver.core.model.expectation;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.NonPropertyExpectation;
import lib.common.util.object.Presentable;

/**
 * Created by rongyu.yan on 4/24/2017.
 */
public class TransientExpectation extends NonPropertyExpectation {
    private int duration;

    public TransientExpectation(Timing timing, Graph graph, int duration) {
        super(timing, graph);
        this.duration = duration;
    }

    @Presentable
    public int getDuration() {
        return duration;
    }
}
