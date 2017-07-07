package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.ui.mobile.extend.TestManager;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public class SelectEndTime extends SelectTime {
    public SelectEndTime(TestManager manager) {
        super(manager);
    }

    @Override
    protected String getTextViewTag() {
        return PeriodicReserve.TV_END_TIME;
    }

    @Override
    protected String getValidityTag() {
        return PeriodicReserve.PROP_END_TIME_VALIDITY;
    }

    @Override
    protected String getExpectedText(String selectedText) {
        return "结束时间：" + selectedText;
    }
}
