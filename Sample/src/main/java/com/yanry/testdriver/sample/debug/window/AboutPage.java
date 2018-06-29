package com.yanry.testdriver.sample.debug.window;

import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.WindowManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/11/2017.
 */
public class AboutPage extends WindowManager.Window {
    public AboutPage(WindowManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        close(new Click<>(new View(this, new ByText("关闭"))), Timing.IMMEDIATELY);
    }
}
