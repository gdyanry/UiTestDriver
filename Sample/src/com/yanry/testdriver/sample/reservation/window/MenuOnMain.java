package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.property.LoginState;
import com.yanry.testdriver.ui.mobile.extend.view.TextView;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByDesc;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class MenuOnMain extends TestManager.Window {
    public static String TV_LOGIN;

    public MenuOnMain(TestManager manager) {
        manager.super();
        registerView(TV_LOGIN, new TextView(this, new ByDesc("登录菜单项")));
    }

    @Override
    protected void addCases() {
        popWindow(getWindow(PeriodicReserve.class), new Click(new View(this, new ByText("周期预定"))), Timing.IMMEDIATELY,
                true,
                false);
        popWindow(getWindow(MyReservation.class), new Click(new View(this, new ByText("我的预订"))), Timing.IMMEDIATELY, true,
                false);
        TextView vLogin = getView(TV_LOGIN);
            LoginState loginState = getProperty(LoginState.class);
        createPath(getCreateEvent(), vLogin.getText().getDynamicExpectation(getGraph(), Timing
                .IMMEDIATELY, () -> loginState.getCurrentValue() ? "登录" : "退出登录"));
        close(new Click(vLogin), Timing.IMMEDIATELY).put(loginState, true);
        popWindow(getWindow(Login.class), new Click(vLogin), Timing.IMMEDIATELY, false, false).put(loginState, false);
        closeOnTouchOutside();
    }
}
