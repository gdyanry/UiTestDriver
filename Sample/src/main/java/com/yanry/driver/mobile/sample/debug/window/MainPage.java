package com.yanry.driver.mobile.sample.debug.window;

import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.WindowManager;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.property.LoginState;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByText;
import com.yanry.driver.mobile.sample.debug.TestApp;

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
        Click clickLogout = new Click(new View(getManager(), this, new ByText("退出登录")));
        popWindow(new LoginPage(getManager()), clickLogout, Timing.IMMEDIATELY, true, true);
        createPath(clickLogout, getProperty(CurrentUser.class).getStaticExpectation(Timing.IMMEDIATELY, false, ""));
        popWindow(new AboutPage(getManager()), new Click<>(new View(getManager(), this, new ByText("关于"))), Timing
                .IMMEDIATELY, false, true);
    }
}
