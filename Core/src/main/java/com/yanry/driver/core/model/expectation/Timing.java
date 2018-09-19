package com.yanry.driver.core.model.expectation;

import lib.common.util.object.Presentable;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
@Presentable
public class Timing {
    public static final Timing IMMEDIATELY = null;

    private boolean isWithin;
    private int millis;

    public Timing(boolean isWithin, int millis) {
        this.isWithin = isWithin;
        this.millis = millis;
    }

    @Presentable
    public boolean isWithin() {
        return isWithin;
    }

    @Presentable
    public int getMillis() {
        return millis;
    }
}
