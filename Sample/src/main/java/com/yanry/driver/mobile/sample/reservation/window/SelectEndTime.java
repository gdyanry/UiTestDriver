package com.yanry.driver.mobile.sample.reservation.window;

import com.yanry.driver.core.extend.WindowManager;
import com.yanry.driver.core.extend.view.TextView;
import com.yanry.driver.mobile.sample.reservation.window.PeriodicReserve.Validity;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public class SelectEndTime extends SelectTime {
    public SelectEndTime(WindowManager manager) {
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
