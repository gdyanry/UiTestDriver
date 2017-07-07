package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.ui.mobile.extend.TestManager;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public class SelectStartTime extends SelectTime {
    public SelectStartTime(TestManager manager) {
        super(manager);
    }

    @Override
    protected String getTextViewTag() {
        return PeriodicReserve.TV_START_TIME;
    }

    @Override
    protected String getValidityTag() {
        return PeriodicReserve.PROP_START_TIME_VALIDITY;
    }

    @Override
    protected String getExpectedText(String selectedText) {
        return "开始时间：" + selectedText;
    }
}
