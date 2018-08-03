package com.yanry.driver.mobile.sample.debug.window;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.LoginPathHandler;
import com.yanry.driver.mobile.WindowManager;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.expectation.RequestDialog;
import com.yanry.driver.mobile.expectation.Toast;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.property.LoginState;
import com.yanry.driver.mobile.sample.debug.NetworkState;
import com.yanry.driver.mobile.sample.debug.TestApp;
import com.yanry.driver.mobile.view.ValidateEditText;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByDesc;
import com.yanry.driver.mobile.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/8/2017.
 */
public abstract class LoginPage extends WindowManager.Window {
    public static String DESC_USER;
    public static String DESC_PWD;

    public LoginPage(WindowManager manager) {
        manager.super();
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        showOnStartUp(new Timing(false, TestApp.PLASH_DURATION)).put(new LoginState(graph, getCurrentUser()), false);
        ValidateEditText etUser = new ValidateEditText(graph, this, new ByDesc(DESC_USER));
        ValidateEditText etPwd = new ValidateEditText(graph, this, new ByDesc(DESC_PWD));
        // 页面打开时输入框内容为空
        createPath(getCreateEvent(), etUser.getContent().getStaticExpectation(Timing.IMMEDIATELY, true, ""));
        createPath(getCreateEvent(), etPwd.getContent().getStaticExpectation(Timing.IMMEDIATELY, true, ""));

        Click clickLogin = new Click(new View(graph, this, new ByText("登录")));
        // 添加输入框用例
        etUser.setEmptyValidationCase(clickLogin, new Toast(Timing.IMMEDIATELY, graph, TestApp.TOAST_DURATION, "用户名不能为空"));
        etUser.addNegativeCase("A lan", clickLogin, new Toast(Timing.IMMEDIATELY, graph, TestApp.TOAST_DURATION, "用户名不能包含空格"));
        etUser.addPositiveCases("daming.wang");
        etPwd.setEmptyValidationCase(clickLogin, new Toast(Timing.IMMEDIATELY, graph, TestApp.TOAST_DURATION, "密码不能为空"), etUser.getValidity());
        etPwd.addNegativeCase("124", clickLogin, new Toast(Timing.IMMEDIATELY, graph, TestApp.TOAST_DURATION, "密码长度不能小于6"), etUser.getValidity());
        etPwd.addPositiveCases("123456");
        // 无网络连接
        createPath(clickLogin, new Toast(Timing.IMMEDIATELY, graph, TestApp.TOAST_DURATION, "无网络连接"))
                .addInitState(getNetworkState(), NetworkState.Network.Disconnected)
                .addInitState(etUser.getValidity(), true)
                .addInitState(etPwd.getValidity(), true);
        // 请求对话框
        createPath(clickLogin, new RequestDialog(Timing.IMMEDIATELY, graph, TestApp.HTTP_TIMEOUT))
                .addInitState(getNetworkState(), NetworkState.Network.Abnormal)
                .addInitState(etUser.getValidity(), true)
                .addInitState(etPwd.getValidity(), true);
        createPath(clickLogin, new RequestDialog(Timing.IMMEDIATELY, graph, TestApp.HTTP_TIMEOUT))
                .addInitState(getNetworkState(), NetworkState.Network.Normal)
                .addInitState(etUser.getValidity(), true)
                .addInitState(etPwd.getValidity(), true);
        // 连接超时
        Timing withinTimeout = new Timing(true, TestApp.HTTP_TIMEOUT);
        createPath(clickLogin, new Toast(withinTimeout, graph, TestApp.TOAST_DURATION, "网络错误"))
                .addInitState(getNetworkState(), NetworkState.Network.Abnormal)
                .addInitState(etUser.getValidity(), true)
                .addInitState(etPwd.getValidity(), true);
        // 请求成功
        LoginPathHandler loginPathHandler = new LoginPathHandler(getCurrentUser(), etUser, etPwd);
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
