package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Presentable;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
@Presentable
public class Timing {
    public static final Timing IMMEDIATELY = new Timing(false, 0);

    private boolean isWithin;
    private int second;

    public Timing(boolean isWithin, int second) {
        this.isWithin = isWithin;
        this.second = second;
    }

    @Presentable
    public boolean isWithin() {
        return isWithin;
    }

    @Presentable
    public int getSecond() {
        return second;
    }
}
