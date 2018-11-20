package com.yanry.driver.mobile.sample.login.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByText;
import com.yanry.driver.mobile.window.Application;
import com.yanry.driver.mobile.window.Window;

/**
 * Created by rongyu.yan on 5/11/2017.
 */
public class AboutPage extends Window {
    public AboutPage(Graph graph, Application manager) {
        super(graph, manager);
    }

    @Override
    protected void addCases(Graph graph, Application manager) {
        close(new Click(new View(graph, this, new ByText("关闭"))), Timing.IMMEDIATELY).addContextValue(this, true);
    }
}
