package com.yanry.driver.mobile.sample.login.window;

import com.yanry.driver.core.model.Divider;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.ValuePredicate;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.core.model.predicate.Within;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.expectation.RequestDialog;
import com.yanry.driver.mobile.expectation.Toast;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.property.EditText;
import com.yanry.driver.mobile.sample.login.Const;
import com.yanry.driver.mobile.sample.login.NetworkState;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByDesc;
import com.yanry.driver.mobile.view.selector.ByText;
import com.yanry.driver.mobile.window.Application;
import com.yanry.driver.mobile.window.Window;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by rongyu.yan on 5/8/2017.
 */
public class LoginPage extends Window {
    public static String DESC_USER;
    public static String DESC_PWD;

    private CurrentUser currentUser;
    private NetworkState networkState;

    public LoginPage(Graph graph, Application manager, CurrentUser currentUser, NetworkState networkState) {
        super(graph, manager);
        this.currentUser = currentUser;
        this.networkState = networkState;
    }

    @Override
    protected void addCases(Graph graph, Application manager) {
        showOnLaunch(new Timing(false, Const.PLASH_DURATION)).addContextValue(currentUser.getLoginState(), false);
        EditText etUser = new EditText(new View(graph, this, new ByDesc(DESC_USER)));
        Divider<String> userValidity = new Divider<>(etUser, new ValuePredicate<>() {
            @Override
            public Stream<String> getConcreteValues() {
                return null;
            }

            @Override
            public boolean test(String value) {
                return value != null && value.length() > 0 && !value.contains(" ");
            }
        });
        EditText etPwd = new EditText(new View(graph, this, new ByDesc(DESC_PWD)));
        Divider<String> pwdValidity = new Divider<>(etPwd, new ValuePredicate<>() {
            @Override
            public Stream<String> getConcreteValues() {
                return null;
            }

            @Override
            public boolean test(String value) {
                return value != null && value.length() >= 6;
            }
        });
        // 页面打开时输入框内容为空
        graph.createPath(getCreateEvent(), etUser.getStaticExpectation(Timing.IMMEDIATELY, true, ""));
        graph.createPath(getCreateEvent(), etPwd.getStaticExpectation(Timing.IMMEDIATELY, true, ""));

        Click clickLogin = new Click(new View(graph, this, new ByText("登录")));
        // 添加输入框用例
        etUser.addValue("daming.wang", "huang.xian");
        graph.createPath(clickLogin, new Toast(Timing.IMMEDIATELY, graph, Const.TOAST_DURATION, "用户名不能为空"))
                .addContextValue(etUser, "")
                .addContextValue(this, true);
        graph.createPath(clickLogin, new Toast(Timing.IMMEDIATELY, graph, Const.TOAST_DURATION, "用户名不能包含空格"))
                .addContextPredicate(etUser, new Within<>(Arrays.asList("A lan", " haha", "yanry ")))
                .addContextValue(this, true);
        etPwd.addValue("123456", "654321");
        graph.createPath(clickLogin, new Toast(Timing.IMMEDIATELY, graph, Const.TOAST_DURATION, "密码不能为空"))
                .addContextValue(userValidity, true)
                .addContextValue(etPwd, "")
                .addContextValue(this, true);
        graph.createPath(clickLogin, new Toast(Timing.IMMEDIATELY, graph, Const.TOAST_DURATION, "密码长度不能小于6"))
                .addContextValue(userValidity, true)
                .addContextPredicate(etPwd, new Within<>(Arrays.asList("124", "05324")))
                .addContextValue(this, true);
        // 无网络连接
        graph.createPath(clickLogin, new Toast(Timing.IMMEDIATELY, graph, Const.TOAST_DURATION, "无网络连接"))
                .addContextValue(networkState, NetworkState.Network.Disconnected)
                .addContextValue(userValidity, true)
                .addContextValue(pwdValidity, true);
        // 请求对话框
        graph.createPath(clickLogin, new RequestDialog(Timing.IMMEDIATELY, graph, Const.HTTP_TIMEOUT))
                .addContextValue(networkState, NetworkState.Network.Abnormal)
                .addContextValue(userValidity, true)
                .addContextValue(pwdValidity, true);
        graph.createPath(clickLogin, new RequestDialog(Timing.IMMEDIATELY, graph, Const.HTTP_TIMEOUT))
                .addContextValue(networkState, NetworkState.Network.Normal)
                .addContextValue(userValidity, true)
                .addContextValue(pwdValidity, true);
        // 连接超时
        Timing withinTimeout = new Timing(true, Const.HTTP_TIMEOUT);
        graph.createPath(clickLogin, new Toast(withinTimeout, graph, Const.TOAST_DURATION, "网络错误"))
                .addContextValue(networkState, NetworkState.Network.Abnormal)
                .addContextValue(userValidity, true)
                .addContextValue(pwdValidity, true);
        for (Map.Entry<String, String> entry : currentUser.getUserPasswordMap().entrySet()) {
            // pop main page
            popWindow(MainPage.class, clickLogin, withinTimeout, true)
                    .addContextValue(networkState, NetworkState.Network.Normal)
                    .addContextValue(etUser, entry.getKey())
                    .addContextValue(etPwd, entry.getValue())
                    .getExpectation().addFollowingExpectation(currentUser.getStaticExpectation(Timing.IMMEDIATELY, false, entry.getKey()));
            // wrong password
            graph.createPath(clickLogin, new Toast(withinTimeout, graph, Const.TOAST_DURATION, "密码错误"))
                    .addContextValue(networkState, NetworkState.Network.Normal)
                    .addContextValue(etUser, entry.getKey())
                    .addContextPredicate(etPwd, Equals.of(entry.getValue()).not())
                    .addContextValue(pwdValidity, true);
        }
        // user not exist
        graph.createPath(clickLogin, new Toast(withinTimeout, graph, Const.TOAST_DURATION, "用户不存在"))
                .addContextValue(networkState, NetworkState.Network.Normal)
                .addContextValue(userValidity, true)
                .addContextValue(pwdValidity, true)
                .addContextPredicate(etUser, new Within<>(currentUser.getUserPasswordMap().keySet()).not());
    }
}
