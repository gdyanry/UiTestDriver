package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.sample.reservation.property.NetworkConnectivity;
import com.yanry.testdriver.ui.mobile.base.event.PassiveSwitchEvent;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.LoginPathHandler;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.expectation.Toast;
import com.yanry.testdriver.ui.mobile.extend.property.CurrentUser;
import com.yanry.testdriver.ui.mobile.extend.view.TextView;
import com.yanry.testdriver.ui.mobile.extend.view.ValidateEditText;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByDesc;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByText;

import static com.yanry.testdriver.sample.reservation.server.Config.*;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class Login extends TestManager.Window {
    public static String ET_USER;
    public static String ET_PWD;
    public static String USER_VALIDATION;
    public static String PWD_VALIDATION;

    public Login(TestManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        ValidateEditText etUser = new ValidateEditText(this, new ByDesc(ET_USER));
        ValidateEditText etPwd = new ValidateEditText(this, new ByDesc(ET_PWD));
        LoginPathHandler loginPathHandler = new LoginPathHandler(getProperty(CurrentUser.class), etUser, etPwd);
        Click clickLogin = new Click(new View(this, new ByText("登录")));
        TextView userErrorView = new TextView(this, new ByDesc(USER_VALIDATION));
        TextView pwdErrorView = new TextView(this, new ByDesc(PWD_VALIDATION));

        createPath(getCreateEvent(), etUser.getInputContent().getExpectation(""));
        createPath(getCreateEvent(), etPwd.getInputContent().getExpectation(""));
        etUser.setEmptyValidationCase(clickLogin, userErrorView.getVisibility().getExpectation(Timing
                .IMMEDIATELY, true).addFollowingExpectation(userErrorView.getText().getExpectation
                (Timing.IMMEDIATELY, "用户名不能为空")));
        etPwd.setEmptyValidationCase(clickLogin, pwdErrorView.getVisibility().getExpectation(Timing
                .IMMEDIATELY, true).addFollowingExpectation(pwdErrorView.getText().getExpectation
                (Timing.IMMEDIATELY, "密码不能为空")), etUser.getValidity());
        // 输入内容时隐藏错误提示视图
        createPath(new PassiveSwitchEvent<>(etUser.getInputContent(), v -> true, v -> true), userErrorView
                .getVisibility().getExpectation(Timing.IMMEDIATELY, false)).addInitState(userErrorView
                .getVisibility(), true);
        createPath(new PassiveSwitchEvent<>(etPwd.getInputContent(), v -> true, v -> true), pwdErrorView
                .getVisibility().getExpectation(Timing.IMMEDIATELY, false)).addInitState(pwdErrorView
                .getVisibility(), true);
        etUser.addPositiveCases("xiaoxiaoming");
        etPwd.addPositiveCases("123456");
        etUser.addNegativeCase("xiao ming", clickLogin, userErrorView.getVisibility().getExpectation
                (Timing.IMMEDIATELY, true).addFollowingExpectation(userErrorView.getText().getExpectation
                (Timing.IMMEDIATELY, "用户名不能含有空格")));
        etPwd.addNegativeCase("123", clickLogin, pwdErrorView.getVisibility().getExpectation(Timing
                .IMMEDIATELY, true).addFollowingExpectation(pwdErrorView.getText().getExpectation
                (Timing.IMMEDIATELY, "密码长度不能小于6个字符")), etUser.getValidity());
        // no connection
        createPath(clickLogin, new Toast(Timing.IMMEDIATELY, TOAST_DURATION, getGraph(),
                TOAST_NO_CONNECTION)).addInitState(etUser.getValidity(), true).addInitState(etPwd.getValidity(), true)
                .addInitState(getProperty(NetworkConnectivity.class), false);
        // login
        Timing withinTimeout = new Timing(true, REQUEST_TIMEOUT);
        loginPathHandler.initStateToInvalidPassword(() -> createPath(clickLogin, new Toast(withinTimeout, TOAST_DURATION,
                getGraph(), "密码错误")).addInitState(getProperty(NetworkConnectivity.class), true));
        loginPathHandler.initStateToInvalidUser(() -> createPath(clickLogin, new Toast(withinTimeout, TOAST_DURATION,
                getGraph(), "用户不存在")).addInitState(getProperty(NetworkConnectivity.class), true));
        loginPathHandler.initStateToSuccessLogin(() -> close(clickLogin, withinTimeout).addInitState(getProperty
                (NetworkConnectivity.class), true));
        loginPathHandler.handleCurrentUserOnSuccessLogin(withinTimeout, e -> createPath(clickLogin, e).addInitState
                (getProperty(NetworkConnectivity.class), true));
    }
}