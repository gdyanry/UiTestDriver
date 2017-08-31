package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.sample.reservation.window.PeriodicReserve.Validity;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.view.TextView;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public class SelectEndTime extends SelectTime {
    public SelectEndTime(TestManager manager) {
        super(manager);
    }

    @Override
    protected TextView getTextView(PeriodicReserve reserve) {
        return reserve.getTvEndTime();
    }

    @Override
    protected Validity getValidity(PeriodicReserve reserve) {
        return reserve.getEndTimeValidity();
    }

    @Override
    protected String getExpectedText(String selectedText) {
        return "结束时间：" + selectedText;
    }
}
