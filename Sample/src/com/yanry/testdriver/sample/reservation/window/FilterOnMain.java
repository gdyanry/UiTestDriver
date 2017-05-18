package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.ui.mobile.extend.TestManager;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class FilterOnMain extends TestManager.Window {
    public FilterOnMain(TestManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        closeOnTouchOutside();
    }
}
