package com.yanry.driver.mobile.sample.login.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/11/2017.
 */
public class AboutPage extends Window {
    public AboutPage(WindowManager manager) {
        super(manager);
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        close(new Click<>(new View(graph, this, new ByText("关闭"))), Timing.IMMEDIATELY);
    }
}
