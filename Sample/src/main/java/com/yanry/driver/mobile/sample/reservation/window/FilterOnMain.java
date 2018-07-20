package com.yanry.driver.mobile.sample.reservation.window;

import com.yanry.driver.core.extend.WindowManager;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class FilterOnMain extends WindowManager.Window {
    public FilterOnMain(WindowManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        closeOnTouchOutside();
    }
}
