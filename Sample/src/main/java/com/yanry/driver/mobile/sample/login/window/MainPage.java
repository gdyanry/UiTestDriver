package com.yanry.driver.mobile.sample.login.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.property.LoginState;
import com.yanry.driver.mobile.sample.login.Const;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByText;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;

/**
 * Created by rongyu.yan on 5/10/2017.
 */
public class MainPage extends Window {
    private CurrentUser currentUser;
    private LoginState loginState;

    public MainPage(Graph graph, WindowManager manager, CurrentUser currentUser, LoginState loginState) {
        super(graph, manager);
        this.currentUser = currentUser;
        this.loginState = loginState;
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        showOnLaunch(new Timing(false, Const.PLASH_DURATION)).addContextState(loginState, true);
        Click clickLogout = new Click(new View(graph, this, new ByText("退出登录")));
        popWindow(LoginPage.class, clickLogout, Timing.IMMEDIATELY, true);
        createForegroundPath(clickLogout, currentUser.getStaticExpectation(Timing.IMMEDIATELY, false, ""));
        popWindow(AboutPage.class, new Click(new View(graph, this, new ByText("关于"))), Timing.IMMEDIATELY, false);
    }
}
