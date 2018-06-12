package com.yanry.testdriver.sample.reservation.window;

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
    private TextView tvLogin;

    public MenuOnMain(TestManager manager) {
        manager.super();
        tvLogin = new TextView(this, new ByDesc("登录菜单项"));
    }

    @Override
    protected void addCases() {
        popWindow(getWindow(PeriodicReserve.class), new Click(new View(this, new ByText("周期预定"))), Timing.IMMEDIATELY,
                true, false);
        popWindow(getWindow(MyReservation.class), new Click(new View(this, new ByText("我的预订"))), Timing.IMMEDIATELY,
                true, false);
        LoginState loginState = getProperty(LoginState.class);
        createPath(getCreateEvent(), tvLogin.getText().getExpectation(Timing
                .IMMEDIATELY, () -> loginState.getCurrentValue(getGraph()) ? "登录" : "退出登录"));
        close(new Click(tvLogin), Timing.IMMEDIATELY).put(loginState, true);
        popWindow(getWindow(Login.class), new Click(tvLogin), Timing.IMMEDIATELY, false, false)
                .put(loginState, false);
        closeOnTouchOutside();
    }
}
