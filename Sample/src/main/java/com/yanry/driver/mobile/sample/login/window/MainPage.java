package com.yanry.driver.mobile.sample.login.window;

import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.sample.login.Const;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByText;
import com.yanry.driver.mobile.window.Application;
import com.yanry.driver.mobile.window.Window;

/**
 * Created by rongyu.yan on 5/10/2017.
 */
public class MainPage extends Window {
    private CurrentUser currentUser;

    public MainPage(StateSpace stateSpace, Application manager, CurrentUser currentUser) {
        super(stateSpace, manager);
        this.currentUser = currentUser;
    }

    @Override
    protected void addCases(StateSpace stateSpace, Application manager) {
        showOnLaunch(new Timing(false, Const.PLASH_DURATION)).addContextValue(currentUser.getLoginState(), true);
        Click clickLogout = new Click(new View(stateSpace, this, new ByText("退出登录")));
        popWindow(LoginPage.class, clickLogout, Timing.IMMEDIATELY, true)
                .getExpectation().addFollowingExpectation(currentUser.getStaticExpectation(Timing.IMMEDIATELY, false, ""));
        popWindow(AboutPage.class, new Click(new View(stateSpace, this, new ByText("关于"))), Timing.IMMEDIATELY, false);
    }
}
