package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Presentable;

/**
 * Created by rongyu.yan on 4/24/2017.
 */
public abstract class TransientExpectation extends StatelessExpectation {
    private int duration;

    public TransientExpectation(Timing timing, int duration) {
        super(timing);
        this.duration = duration;
    }

    @Presentable
    public int getDuration() {
        return duration;
    }
}
