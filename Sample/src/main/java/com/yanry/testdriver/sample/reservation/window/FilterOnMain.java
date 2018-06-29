package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.ui.mobile.extend.WindowManager;

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
