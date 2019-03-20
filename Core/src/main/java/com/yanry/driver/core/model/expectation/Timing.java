package com.yanry.driver.core.model.expectation;

import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.HandyObject;
import yanry.lib.java.util.object.Visible;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
public class Timing extends HandyObject {
    public static final Timing IMMEDIATELY = null;

    private boolean isWithin;
    private int millis;

    public Timing(boolean isWithin, int millis) {
        this.isWithin = isWithin;
        this.millis = millis;
    }

    @EqualsPart
    @Visible
    public boolean isWithin() {
        return isWithin;
    }

    @EqualsPart
    @Visible
    public int getMillis() {
        return millis;
    }
}
