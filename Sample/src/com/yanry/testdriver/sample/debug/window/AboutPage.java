package com.yanry.testdriver.sample.debug.window;

import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/11/2017.
 */
public class AboutPage extends TestManager.Window {
    public AboutPage(TestManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        close(new Click<>(new View(this, new ByText("关闭"))), Timing.IMMEDIATELY);
    }
}
