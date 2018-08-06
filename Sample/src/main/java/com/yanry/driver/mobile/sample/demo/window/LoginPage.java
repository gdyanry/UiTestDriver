package com.yanry.driver.mobile.sample.demo.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.model.LoginPathHandler;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.expectation.RequestDialog;
import com.yanry.driver.mobile.expectation.Toast;
import com.yanry.driver.mobile.property.*;
import com.yanry.driver.mobile.sample.demo.NetworkState;
import com.yanry.driver.mobile.sample.demo.TestApp;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByDesc;
import com.yanry.driver.mobile.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/8/2017.
 */
public abstract class LoginPage extends Window {
    public static String DESC_USER;
    public static String DESC_PWD;

    public LoginPage(WindowManager manager) {
        super(manager);
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        showOnStartUp(new Timing(false, TestApp.PLASH_DURATION)).addInitState(new LoginState(graph, getCurrentUser()), false);
        View etUser = new View(graph, this, new ByDesc(DESC_USER));
        Text txtUser = new EditableText(etUser);
        TextValidity userValidity = new TextValidity(etUser, txtUser);
        View etPwd = new View(graph, this, new ByDesc(DESC_PWD));
        Text txtPwd = new EditableText(etPwd);
        TextValidity pwdValidity = new TextValidity(etPwd, txtPwd);
        // 页面打开时输入框内容为空
        createPath(getCreateEvent(), txtUser.getStaticExpectation(Timing.IMMEDIATELY, true, ""));
        createPath(getCreateEvent(), txtPwd.getStaticExpectation(Timing.IMMEDIATELY, true, ""));

        Click clickLogin = new Click(new View(graph, this, new ByText("登录")));
        // 添加输入框用例
        userValidity.addNegativeCase("", clickLogin, new Toast(Timing.IMMEDIATELY, graph, TestApp.TOAST_DURATION, "用户名不能为空"));
        userValidity.addNegativeCase("A lan", clickLogin, new Toast(Timing.IMMEDIATELY, graph, TestApp.TOAST_DURATION, "用户名不能包含空格"));
        userValidity.addPositiveCases("daming.wang");
        pwdValidity.addNegativeCase("", clickLogin, new Toast(Timing.IMMEDIATELY, graph, TestApp.TOAST_DURATION, "密码不能为空"), userValidity);
        pwdValidity.addNegativeCase("124", clickLogin, new Toast(Timing.IMMEDIATELY, graph, TestApp.TOAST_DURATION, "密码长度不能小于6"), userValidity);
        pwdValidity.addPositiveCases("123456");
        // 无网络连接
        createPath(clickLogin, new Toast(Timing.IMMEDIATELY, graph, TestApp.TOAST_DURATION, "无网络连接"))
                .addInitState(getNetworkState(), NetworkState.Network.Disconnected)
                .addInitState(userValidity, true)
                .addInitState(pwdValidity, true);
        // 请求对话框
        createPath(clickLogin, new RequestDialog(Timing.IMMEDIATELY, graph, TestApp.HTTP_TIMEOUT))
                .addInitState(getNetworkState(), NetworkState.Network.Abnormal)
                .addInitState(userValidity, true)
                .addInitState(pwdValidity, true);
        createPath(clickLogin, new RequestDialog(Timing.IMMEDIATELY, graph, TestApp.HTTP_TIMEOUT))
                .addInitState(getNetworkState(), NetworkState.Network.Normal)
                .addInitState(userValidity, true)
                .addInitState(pwdValidity, true);
        // 连接超时
        Timing withinTimeout = new Timing(true, TestApp.HTTP_TIMEOUT);
        createPath(clickLogin, new Toast(withinTimeout, graph, TestApp.TOAST_DURATION, "网络错误"))
                .addInitState(getNetworkState(), NetworkState.Network.Abnormal)
                .addInitState(userValidity, true)
                .addInitState(pwdValidity, true);
        // 请求成功
        LoginPathHandler loginPathHandler = new LoginPathHandler(getCurrentUser(), userValidity, pwdValidity);
        // login state
        loginPathHandler.handleCurrentUserOnSuccessLogin(withinTimeout, e -> createPath(clickLogin, e)
                .addInitState(getNetworkState(), NetworkState.Network.Normal));
        // pop main page
        loginPathHandler.initStateToSuccessLogin(() -> popWindow(getMainPage(), clickLogin, withinTimeout, true)
                .addInitState(getNetworkState(), NetworkState.Network.Normal));
        // business error
        loginPathHandler.initStateToInvalidUser(() -> createPath(clickLogin, new Toast(withinTimeout, graph, TestApp.TOAST_DURATION, "用户不存在"))
                .addInitState(getNetworkState(), NetworkState.Network.Normal));
        loginPathHandler.initStateToInvalidPassword(() -> createPath(clickLogin, new Toast(withinTimeout, graph, TestApp.TOAST_DURATION, "密码错误"))
                .addInitState(getNetworkState(), NetworkState.Network.Normal));
    }

    protected abstract CurrentUser getCurrentUser();

    protected abstract NetworkState getNetworkState();

    protected abstract MainPage getMainPage();
}
