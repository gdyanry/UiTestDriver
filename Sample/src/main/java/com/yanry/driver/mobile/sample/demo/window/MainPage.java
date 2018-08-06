package com.yanry.driver.mobile.sample.demo.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.property.LoginState;
import com.yanry.driver.mobile.sample.demo.TestApp;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/10/2017.
 */
public abstract class MainPage extends Window {

    public MainPage(WindowManager manager) {
        super(manager);
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        showOnStartUp(new Timing(false, TestApp.PLASH_DURATION)).addInitState(new LoginState(graph, getCurrentUser()), true);
        Click clickLogout = new Click(new View(graph, this, new ByText("退出登录")));
        popWindow(getLoginPage(), clickLogout, Timing.IMMEDIATELY, true);
        createPath(clickLogout, getCurrentUser().getStaticExpectation(Timing.IMMEDIATELY, false, ""));
        popWindow(getAboutPage(), new Click<>(new View(graph, this, new ByText("关于"))), Timing.IMMEDIATELY, false);
    }

    protected abstract CurrentUser getCurrentUser();

    protected abstract LoginPage getLoginPage();

    protected abstract AboutPage getAboutPage();
}
