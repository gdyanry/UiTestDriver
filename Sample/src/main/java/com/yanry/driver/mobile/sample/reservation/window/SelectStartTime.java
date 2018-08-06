package com.yanry.driver.mobile.sample.reservation.window;

import com.yanry.driver.mobile.window.WindowManager;
import com.yanry.driver.mobile.property.Text;
import com.yanry.driver.mobile.sample.reservation.window.PeriodicReserve.Validity;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public class SelectStartTime extends SelectTime {
    public SelectStartTime(WindowManager manager) {
        super(manager);
    }

    @Override
    protected Text getTextView(PeriodicReserve reserve) {
        return reserve.getTxtStartTime();
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
