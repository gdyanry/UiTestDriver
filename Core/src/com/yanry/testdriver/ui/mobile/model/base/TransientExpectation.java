package com.yanry.testdriver.ui.mobile.model.base;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
@Presentable
public class TransientExpectation implements Expectation {
    private Timing timing;
    private int duration;

    public TransientExpectation(Timing timing, int duration) {
        this.timing = timing;
        this.duration = duration;
    }

    @Presentable
    public Timing getTiming() {
        return timing;
    }

    @Presentable
    public int getDuration() {
        return duration;
    }
}
