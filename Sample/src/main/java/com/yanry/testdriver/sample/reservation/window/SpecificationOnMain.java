package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.ui.mobile.extend.TestManager;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class SpecificationOnMain extends TestManager.Window {
    public SpecificationOnMain(TestManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        closeOnTouchOutside();
    }
}
