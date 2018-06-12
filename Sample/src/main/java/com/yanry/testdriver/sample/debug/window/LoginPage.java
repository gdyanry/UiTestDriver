package com.yanry.testdriver.sample.debug.window;

import com.yanry.testdriver.sample.debug.NetworkState;
import com.yanry.testdriver.sample.debug.TestApp;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.LoginPathHandler;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.expectation.RequestDialog;
import com.yanry.testdriver.ui.mobile.extend.expectation.Toast;
import com.yanry.testdriver.ui.mobile.extend.property.CurrentUser;
import com.yanry.testdriver.ui.mobile.extend.property.LoginState;
import com.yanry.testdriver.ui.mobile.extend.view.ValidateEditText;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByDesc;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/8/2017.
 */
public class LoginPage extends TestManager.Window {
    public static String DESC_USER;
    public static String DESC_PWD;

    public LoginPage(TestManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        showOnStartUp(new Timing(false, TestApp.PLASH_DURATION)).put(getProperty(LoginState.class),
                false);
        ValidateEditText etUser = new ValidateEditText(this, new ByDesc(DESC_USER));
        ValidateEditText etPwd = new ValidateEditText(this, new ByDesc(DESC_PWD));
        // 页面打开时输入框内容为空
        createPath(getCreateEvent(), etUser.getInputContent().getExpectation(Timing.IMMEDIATELY, ""));
        createPath(getCreateEvent(), etPwd.getInputContent().getExpectation(Timing.IMMEDIATELY, ""));

        Click clickLogin = new Click(new View(this, new ByText("登录")));
        // 添加输入框用例
        etUser.setEmptyValidationCase(clickLogin, new Toast(Timing.IMMEDIATELY, TestApp.TOAST_DURATION,
                "用户名不能为空"));
        etUser.addNegativeCase("A lan", clickLogin, new Toast(Timing.IMMEDIATELY, TestApp
                .TOAST_DURATION, "用户名不能包含空格"));
        etUser.addPositiveCases("daming.wang");
        etPwd.setEmptyValidationCase(clickLogin, new Toast(Timing.IMMEDIATELY, TestApp.TOAST_DURATION,
                "密码不能为空"), etUser.getValidity());
        etPwd.addNegativeCase("124", clickLogin, new Toast(Timing.IMMEDIATELY, TestApp.TOAST_DURATION,
                "密码长度不能小于6"), etUser.getValidity());
        etPwd.addPositiveCases("123456");
        // 无网络连接
        NetworkState networkState = getProperty(NetworkState.class);
        createPath(clickLogin, new Toast(Timing.IMMEDIATELY, TestApp.TOAST_DURATION, "无网络连接"))
                .addInitState(networkState, NetworkState.Network.Disconnected).addInitState(etUser.getValidity(),
                true).addInitState(etPwd.getValidity(), true);
        // 请求对话框
        createPath(clickLogin, new RequestDialog(Timing.IMMEDIATELY, TestApp.HTTP_TIMEOUT)).addInitState
                (networkState, NetworkState.Network.Abnormal).addInitState(etUser.getValidity(), true).addInitState
                (etPwd.getValidity(), true);
        createPath(clickLogin, new RequestDialog(Timing.IMMEDIATELY, TestApp.HTTP_TIMEOUT)).addInitState(networkState,
                NetworkState.Network.Normal).addInitState(etUser.getValidity(), true).addInitState(etPwd.getValidity
                (), true);
        // 连接超时
        Timing withinTimeout = new Timing(true, TestApp.HTTP_TIMEOUT);
        createPath(clickLogin, new Toast(withinTimeout, TestApp.TOAST_DURATION, "网络错误"))
                .addInitState(networkState, NetworkState.Network.Abnormal).addInitState(etUser.getValidity(), true)
                .addInitState(etPwd.getValidity(), true);
        // 请求成功
        LoginPathHandler loginPathHandler = new LoginPathHandler(getProperty(CurrentUser.class), etUser, etPwd);
        // login state
        loginPathHandler.handleCurrentUserOnSuccessLogin(withinTimeout, e -> createPath(
                clickLogin, e).addInitState(networkState, NetworkState.Network.Normal));
        // pop main page
        loginPathHandler.initStateToSuccessLogin(() -> popWindow(getWindow(MainPage.class), clickLogin,
                withinTimeout, true, true).addInitState(networkState, NetworkState
                .Network.Normal));
        // business error
        loginPathHandler.initStateToInvalidUser(() -> createPath(clickLogin, new Toast
                (withinTimeout, TestApp.TOAST_DURATION, "用户不存在")).addInitState(networkState, NetworkState
                .Network.Normal));
        loginPathHandler.initStateToInvalidPassword(() -> createPath(clickLogin, new Toast
                (withinTimeout, TestApp.TOAST_DURATION, "密码错误")).addInitState(networkState, NetworkState
                .Network.Normal));
    }
}
