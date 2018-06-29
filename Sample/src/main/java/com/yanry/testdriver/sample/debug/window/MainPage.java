package com.yanry.testdriver.sample.debug.window;

import com.yanry.testdriver.sample.debug.TestApp;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.WindowManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.property.CurrentUser;
import com.yanry.testdriver.ui.mobile.extend.property.LoginState;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/10/2017.
 */
public class MainPage extends WindowManager.Window {
    public MainPage(WindowManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        showOnStartUp(new Timing(false, TestApp.PLASH_DURATION)).put(getProperty(LoginState.class),
                true);
        Click clickLogout = new Click(new View(this, new ByText("退出登录")));
        popWindow(new LoginPage(getManager()), clickLogout, Timing.IMMEDIATELY, true, true);
        createPath(clickLogout, getProperty(CurrentUser.class).getExpectation
                (Timing.IMMEDIATELY, ""));
        popWindow(new AboutPage(getManager()), new Click<>(new View(this, new ByText("关于"))), Timing
                .IMMEDIATELY, false, true);
    }
}
