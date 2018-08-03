package com.yanry.driver.mobile.sample.reservation.window;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.WindowManager;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.property.LoginState;
import com.yanry.driver.mobile.view.TextView;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByDesc;
import com.yanry.driver.mobile.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class MenuOnMain extends WindowManager.Window {

    public MenuOnMain(WindowManager manager) {
        manager.super();
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        TextView tvLogin = new TextView(graph, this, new ByDesc("登录菜单项"));
        popWindow(new PeriodicReserve(manager), new Click(new View(graph, this, new ByText("周期预定"))), Timing.IMMEDIATELY, true);
        popWindow(new MyReservation(manager), new Click(new View(graph, this, new ByText("我的预订"))), Timing.IMMEDIATELY, true);
        LoginState loginState = new LoginState(graph, new CurrentUser(graph));
        createPath(getCreateEvent(), tvLogin.getText().getDynamicExpectation(Timing.IMMEDIATELY, true, () -> loginState.getCurrentValue() ? "登录" : "退出登录"));
        close(new Click(tvLogin), Timing.IMMEDIATELY).put(loginState, true);
        popWindow(new Login(getManager()), new Click(tvLogin), Timing.IMMEDIATELY, false).put(loginState, false);
        closeOnTouchOutside();
    }
}
