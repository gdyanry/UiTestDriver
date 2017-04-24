package com.yanry.testdriver.sample.reservation.server;

import com.yanry.testdriver.sample.reservation.property.CurrentUser;
import com.yanry.testdriver.server.spring.CommunicatorController;
import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.*;
import com.yanry.testdriver.ui.mobile.base.StateProperty;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.view.TextView;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByDesc;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByText;
import com.yanry.testdriver.ui.mobile.extend.window.Window;
import com.yanry.testdriver.ui.mobile.extend.window.WindowManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by rongyu.yan on 4/18/2017.
 */
@RestController
@RequestMapping("reservation")
public class ReservationController extends CommunicatorController {
    public ReservationController() {
        super(600);
    }

    @Override
    protected void populateGraph(Graph graph, WindowManager manager) {
        String emptyUser = "";
        String user1 = "xiaoming.wang";
        String user2 = "daming.wang";
        String pwd = "aaa111";
        CurrentUser currentUser = new CurrentUser(graph, emptyUser, user1, user2);
        StateProperty<Boolean> loginState = new StateProperty<Boolean>() {
            @Override
            public Boolean checkValue() {
                return !emptyUser.equals(currentUser.getCurrentValue());
            }

            @Override
            protected Graph getGraph() {
                return graph;
            }

            @Override
            public boolean ifNeedVerification() {
                return false;
            }

            @Override
            public boolean transitTo(Predicate<Boolean> to, List<Path> superPathContainer) {
                if (to.test(true)) {
                    return graph.transitToState(currentUser, u -> !emptyUser.equals(u), superPathContainer);
                }
                return graph.transitToState(currentUser, u -> emptyUser.equals(u), superPathContainer);
            }
        };

        Window wMain = new Window("首页", graph, manager);
        Window wMenu = new Window("首页菜单弹窗", graph, manager);
        Window wLogin = new Window("登录页", graph, manager);
        Window wFilter = new Window("筛选弹窗", graph, manager);
        Window wSelectDate = new Window("选择日期弹窗", graph, manager);
        Window wInfo = new Window("图标说明弹窗", graph, manager);
        Window wBook = new Window("预订弹窗", graph, manager);
        Window wRecurrentBook = new Window("周期预订页", graph, manager);
        Window wSelectRoom = new Window("会议室选择页", graph, manager);
        Window wMyReservation = new Window("我的预订", graph, manager);

        // main
        wMain.showOnStartUp(Timing.IMMEDIATELY);
        wMain.popWindow(wSelectDate, new Click(new View(wMain, new ByDesc("选择日期图标"))), Timing.IMMEDIATELY, false,
                false);
        wMain.popWindow(wFilter, new Click(new View(wMain, new ByText("筛选"))), Timing.IMMEDIATELY, false,
                false);
        wMain.popWindow(wInfo, new Click(new View(wMain, new ByDesc("问号图标"))), Timing.IMMEDIATELY, false,
                false);
        wMain.popWindow(wMenu, new Click(new View(wMain, new ByDesc("菜单图标"))), Timing.IMMEDIATELY, false,
                false);

        // menu
        wMenu.popWindow(wRecurrentBook, new Click(new View(wMenu, new ByText("周期预定"))), Timing.IMMEDIATELY, true,
                false);
        wMenu.popWindow(wMyReservation, new Click(new View(wMenu, new ByText("我的预订"))), Timing.IMMEDIATELY, true,
                false);
        TextView vLogin = new TextView(wMenu, new ByDesc("登录菜单项"));
        Util.createPath(graph, null, wMenu.getCreateEvent(), vLogin.getText().getExpectation(graph, Timing
                .IMMEDIATELY, () -> loginState.getCurrentValue() ? "登录" : "退出登录"));
        wMenu.close(new Click(vLogin), Timing.IMMEDIATELY).put(loginState, true);
        wMenu.popWindow(wLogin, new Click(vLogin), Timing.IMMEDIATELY, false, false).put(loginState, false);
        wMenu.closeOnTouchOutside();
    }
}
