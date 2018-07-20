package com.yanry.driver.mobile.sample.reservation.window;

import com.yanry.driver.core.extend.WindowManager;
import com.yanry.driver.core.extend.action.Click;
import com.yanry.driver.core.extend.property.LoginState;
import com.yanry.driver.core.extend.view.TextView;
import com.yanry.driver.core.extend.view.View;
import com.yanry.driver.core.extend.view.selector.ByDesc;
import com.yanry.driver.core.extend.view.selector.ByText;
import com.yanry.driver.core.model.expectation.Timing;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class MenuOnMain extends WindowManager.Window {
    private TextView tvLogin;

    public MenuOnMain(WindowManager manager) {
        manager.super();
        tvLogin = new TextView(getManager(), this, new ByDesc("登录菜单项"));
    }

    @Override
    protected void addCases() {
        popWindow(new PeriodicReserve(getManager()), new Click(new View(getManager(), this, new ByText("周期预定"))), Timing.IMMEDIATELY,true, false);
        popWindow(new MyReservation(getManager()), new Click(new View(getManager(), this, new ByText("我的预订"))), Timing.IMMEDIATELY,true, false);
        LoginState loginState = getProperty(LoginState.class);
        createPath(getCreateEvent(), tvLogin.getText().getDynamicExpectation(Timing.IMMEDIATELY, true, () -> loginState.getCurrentValue() ? "登录" : "退出登录"));
        close(new Click(tvLogin), Timing.IMMEDIATELY).put(loginState, true);
        popWindow(new Login(getManager()), new Click(tvLogin), Timing.IMMEDIATELY, false, false).put(loginState, false);
        closeOnTouchOutside();
    }
}
