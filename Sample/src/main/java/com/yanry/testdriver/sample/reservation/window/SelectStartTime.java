package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.sample.reservation.window.PeriodicReserve.Validity;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.view.TextView;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public class SelectStartTime extends SelectTime {
    public SelectStartTime(TestManager manager) {
        super(manager);
    }

    @Override
    protected TextView getTextView(PeriodicReserve reserve) {
        return reserve.getTvStartTime();
    }

    @Override
    protected Validity getValidity(PeriodicReserve reserve) {
        return reserve.getStartTimeValidity();
    }

    @Override
    protected String getExpectedText(String selectedText) {
        return "开始时间：" + selectedText;
    }
}
