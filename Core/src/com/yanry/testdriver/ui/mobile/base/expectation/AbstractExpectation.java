package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;

/**
 * Created by rongyu.yan on 4/24/2017.
 */
@Presentable
public abstract class AbstractExpectation implements Expectation {
    private Timing timing;

    public AbstractExpectation(Timing timing) {
        this.timing = timing;
    }

    @Presentable
    public Timing getTiming() {
        return timing;
    }

    protected abstract Graph getGraph();
}
