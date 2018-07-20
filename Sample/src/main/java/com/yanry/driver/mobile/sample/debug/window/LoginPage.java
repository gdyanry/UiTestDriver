package com.yanry.driver.mobile.sample.debug.window;

import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.LoginPathHandler;
import com.yanry.driver.mobile.WindowManager;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.expectation.RequestDialog;
import com.yanry.driver.mobile.expectation.Toast;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.property.LoginState;
import com.yanry.driver.mobile.view.ValidateEditText;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByDesc;
import com.yanry.driver.mobile.view.selector.ByText;
import com.yanry.driver.mobile.sample.debug.NetworkState;
import com.yanry.driver.mobile.sample.debug.TestApp;

/**
 * Created by rongyu.yan on 5/8/2017.
 */
public class LoginPage extends WindowManager.Window {
    public static String DESC_USER;
    public static String DESC_PWD;

    public LoginPage(WindowManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        showOnStartUp(new Timing(false, TestApp.PLASH_DURATION)).put(getProperty(LoginState.class), false);
        ValidateEditText etUser = new ValidateEditText(getManager(), this, new ByDesc(DESC_USER));
        ValidateEditText etPwd = new ValidateEditText(getManager(), this, new ByDesc(DESC_PWD));
        // 页面打开时输入框内容为空
        createPath(getCreateEvent(), etUser.getContent().getStaticExpectation(Timing.IMMEDIATELY, true, ""));
        createPath(getCreateEvent(), etPwd.getContent().getStaticExpectation(Timing.IMMEDIATELY, true, ""));

        Click clickLogin = new Click(new View(getManager(), this, new ByText("登录")));
        // 添加输入框用例
        etUser.setEmptyValidationCase(clickLogin, new Toast(Timing.IMMEDIATELY, getManager(), TestApp.TOAST_DURATION,
                "用户名不能为空"));
        etUser.addNegativeCase("A lan", clickLogin, new Toast(Timing.IMMEDIATELY, getManager(), TestApp
                .TOAST_DURATION, "用户名不能包含空格"));
        etUser.addPositiveCases("daming.wang");
        etPwd.setEmptyValidationCase(clickLogin, new Toast(Timing.IMMEDIATELY, getManager(), TestApp.TOAST_DURATION,
                "密码不能为空"), etUser.getValidity());
        etPwd.addNegativeCase("124", clickLogin, new Toast(Timing.IMMEDIATELY, getManager(), TestApp.TOAST_DURATION,
                "密码长度不能小于6"), etUser.getValidity());
        etPwd.addPositiveCases("123456");
        // 无网络连接
        NetworkState networkState = getProperty(NetworkState.class);
        createPath(clickLogin, new Toast(Timing.IMMEDIATELY, getManager(), TestApp.TOAST_DURATION, "无网络连接"))
                .addInitState(networkState, NetworkState.Network.Disconnected).addInitState(etUser.getValidity(),
                true).addInitState(etPwd.getValidity(), true);
        // 请求对话框
        createPath(clickLogin, new RequestDialog(Timing.IMMEDIATELY, getManager(), TestApp.HTTP_TIMEOUT)).addInitState
                (networkState, NetworkState.Network.Abnormal).addInitState(etUser.getValidity(), true).addInitState
                (etPwd.getValidity(), true);
        createPath(clickLogin, new RequestDialog(Timing.IMMEDIATELY, getManager(), TestApp.HTTP_TIMEOUT)).addInitState(networkState,
                NetworkState.Network.Normal).addInitState(etUser.getValidity(), true).addInitState(etPwd.getValidity
                (), true);
        // 连接超时
        Timing withinTimeout = new Timing(true, TestApp.HTTP_TIMEOUT);
        createPath(clickLogin, new Toast(withinTimeout, getManager(), TestApp.TOAST_DURATION, "网络错误"))
                .addInitState(networkState, NetworkState.Network.Abnormal).addInitState(etUser.getValidity(), true)
                .addInitState(etPwd.getValidity(), true);
        // 请求成功
        LoginPathHandler loginPathHandler = new LoginPathHandler(getProperty(CurrentUser.class), etUser, etPwd);
        // login state
        loginPathHandler.handleCurrentUserOnSuccessLogin(withinTimeout, e -> createPath(
                clickLogin, e).addInitState(networkState, NetworkState.Network.Normal));
        // pop main page
        loginPathHandler.initStateToSuccessLogin(() -> popWindow(new MainPage(getManager()), clickLogin,
                withinTimeout, true, true).addInitState(networkState, NetworkState
                .Network.Normal));
        // business error
        loginPathHandler.initStateToInvalidUser(() -> createPath(clickLogin, new Toast
                (withinTimeout, getManager(), TestApp.TOAST_DURATION, "用户不存在")).addInitState(networkState, NetworkState
                .Network.Normal));
        loginPathHandler.initStateToInvalidPassword(() -> createPath(clickLogin, new Toast
                (withinTimeout, getManager(), TestApp.TOAST_DURATION, "密码错误")).addInitState(networkState, NetworkState
                .Network.Normal));
    }
}
