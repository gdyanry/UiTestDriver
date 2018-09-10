package com.yanry.driver.mobile.sample.login.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.expectation.RequestDialog;
import com.yanry.driver.mobile.expectation.Toast;
import com.yanry.driver.mobile.model.LoginPathHandler;
import com.yanry.driver.mobile.property.*;
import com.yanry.driver.mobile.sample.login.Const;
import com.yanry.driver.mobile.sample.login.NetworkState;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByDesc;
import com.yanry.driver.mobile.view.selector.ByText;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;

/**
 * Created by rongyu.yan on 5/8/2017.
 */
public class LoginPage extends Window {
    public static String DESC_USER;
    public static String DESC_PWD;
    
    private CurrentUser currentUser;
    private LoginState loginState;
    private NetworkState networkState;

    public LoginPage(Graph graph, WindowManager manager, CurrentUser currentUser, LoginState loginState, NetworkState networkState) {
        super(graph, manager);
        this.currentUser = currentUser;
        this.loginState = loginState;
        this.networkState = networkState;
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        showOnLaunch(new Timing(false, Const.PLASH_DURATION)).addContextState(loginState, false);
        View etUser = new View(graph, this, new ByDesc(DESC_USER));
        Text txtUser = new EditableText(etUser);
        TextValidity userValidity = new TextValidity(etUser, txtUser);
        View etPwd = new View(graph, this, new ByDesc(DESC_PWD));
        Text txtPwd = new EditableText(etPwd);
        TextValidity pwdValidity = new TextValidity(etPwd, txtPwd);
        // 页面打开时输入框内容为空
        createForegroundPath(getCreateEvent(), txtUser.getStaticExpectation(Timing.IMMEDIATELY, true, ""));
        createForegroundPath(getCreateEvent(), txtPwd.getStaticExpectation(Timing.IMMEDIATELY, true, ""));

        Click clickLogin = new Click(new View(graph, this, new ByText("登录")));
        // 添加输入框用例
        userValidity.addNegativeCase("", clickLogin, new Toast(Timing.IMMEDIATELY, graph, Const.TOAST_DURATION, "用户名不能为空"));
        userValidity.addNegativeCase("A lan", clickLogin, new Toast(Timing.IMMEDIATELY, graph, Const.TOAST_DURATION, "用户名不能包含空格"));
        userValidity.addPositiveCases("daming.wang");
        pwdValidity.addNegativeCase("", clickLogin, new Toast(Timing.IMMEDIATELY, graph, Const.TOAST_DURATION, "密码不能为空"), userValidity);
        pwdValidity.addNegativeCase("124", clickLogin, new Toast(Timing.IMMEDIATELY, graph, Const.TOAST_DURATION, "密码长度不能小于6"), userValidity);
        pwdValidity.addPositiveCases("123456");
        // 无网络连接
        createForegroundPath(clickLogin, new Toast(Timing.IMMEDIATELY, graph, Const.TOAST_DURATION, "无网络连接"))
                .addContextState(networkState, NetworkState.Network.Disconnected)
                .addContextState(userValidity, true)
                .addContextState(pwdValidity, true);
        // 请求对话框
        createForegroundPath(clickLogin, new RequestDialog(Timing.IMMEDIATELY, graph, Const.HTTP_TIMEOUT))
                .addContextState(networkState, NetworkState.Network.Abnormal)
                .addContextState(userValidity, true)
                .addContextState(pwdValidity, true);
        createForegroundPath(clickLogin, new RequestDialog(Timing.IMMEDIATELY, graph, Const.HTTP_TIMEOUT))
                .addContextState(networkState, NetworkState.Network.Normal)
                .addContextState(userValidity, true)
                .addContextState(pwdValidity, true);
        // 连接超时
        Timing withinTimeout = new Timing(true, Const.HTTP_TIMEOUT);
        createForegroundPath(clickLogin, new Toast(withinTimeout, graph, Const.TOAST_DURATION, "网络错误"))
                .addContextState(networkState, NetworkState.Network.Abnormal)
                .addContextState(userValidity, true)
                .addContextState(pwdValidity, true);
        // 请求成功
        LoginPathHandler loginPathHandler = new LoginPathHandler(currentUser, userValidity, pwdValidity);
        // login state
        loginPathHandler.handleCurrentUserOnSuccessLogin(withinTimeout, e -> createForegroundPath(clickLogin, e)
                .addContextState(networkState, NetworkState.Network.Normal));
        // pop main page
        loginPathHandler.initStateToSuccessLogin(() -> popWindow(MainPage.class, clickLogin, withinTimeout, true)
                .addContextState(networkState, NetworkState.Network.Normal));
        // business error
        loginPathHandler.initStateToInvalidUser(() -> createForegroundPath(clickLogin, new Toast(withinTimeout, graph, Const.TOAST_DURATION, "用户不存在"))
                .addContextState(networkState, NetworkState.Network.Normal));
        loginPathHandler.initStateToInvalidPassword(() -> createForegroundPath(clickLogin, new Toast(withinTimeout, graph, Const.TOAST_DURATION, "密码错误"))
                .addContextState(networkState, NetworkState.Network.Normal));
    }
}
