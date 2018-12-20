package com.yanry.driver.mobile.sample.login.window;

import com.yanry.driver.core.model.base.StateSpace;
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
    public AboutPage(StateSpace stateSpace, Application manager) {
        super(stateSpace, manager);
    }

    @Override
    protected void addCases(StateSpace stateSpace, Application manager) {
        close(new Click(new View(stateSpace, this, new ByText("关闭"))), Timing.IMMEDIATELY).addContextValue(this, true);
    }
}
