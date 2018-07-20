package com.yanry.driver.mobile.sample.reservation.window;

import com.yanry.driver.core.extend.LoginPathHandler;
import com.yanry.driver.core.extend.WindowManager;
import com.yanry.driver.core.extend.action.Click;
import com.yanry.driver.core.extend.expectation.Toast;
import com.yanry.driver.core.extend.property.CurrentUser;
import com.yanry.driver.core.extend.view.TextView;
import com.yanry.driver.core.extend.view.ValidateEditText;
import com.yanry.driver.core.extend.view.View;
import com.yanry.driver.core.extend.view.selector.ByDesc;
import com.yanry.driver.core.extend.view.selector.ByText;
import com.yanry.driver.core.model.event.StateChangeCallback;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.sample.reservation.property.NetworkConnectivity;

import static com.yanry.driver.mobile.sample.reservation.server.Config.*;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class Login extends WindowManager.Window {
    public static String ET_USER;
    public static String ET_PWD;
    public static String USER_VALIDATION;
    public static String PWD_VALIDATION;

    public Login(WindowManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        ValidateEditText etUser = new ValidateEditText(getManager(), this, new ByDesc(ET_USER));
        ValidateEditText etPwd = new ValidateEditText(getManager(), this, new ByDesc(ET_PWD));
        LoginPathHandler loginPathHandler = new LoginPathHandler(getProperty(CurrentUser.class), etUser, etPwd);
        Click clickLogin = new Click(new View(getManager(), this, new ByText("登录")));
        TextView userErrorView = new TextView(getManager(), this, new ByDesc(USER_VALIDATION));
        TextView pwdErrorView = new TextView(getManager(), this, new ByDesc(PWD_VALIDATION));

        createPath(getCreateEvent(), etUser.getContent().getStaticExpectation(Timing.IMMEDIATELY, true, ""));
        createPath(getCreateEvent(), etPwd.getContent().getStaticExpectation(Timing.IMMEDIATELY, true, ""));
        etUser.setEmptyValidationCase(clickLogin, userErrorView.getVisibility().getStaticExpectation(Timing.IMMEDIATELY, true, true)
                .addFollowingExpectation(userErrorView.getText().getStaticExpectation(Timing.IMMEDIATELY, true, "用户名不能为空")));
        etPwd.setEmptyValidationCase(clickLogin, pwdErrorView.getVisibility().getStaticExpectation(Timing.IMMEDIATELY, true, true)
                .addFollowingExpectation(pwdErrorView.getText().getStaticExpectation(Timing.IMMEDIATELY, true, "密码不能为空")), etUser.getValidity());
        // 输入内容时隐藏错误提示视图
        createPath(new StateChangeCallback<>(etUser.getContent(), v -> true, v -> true), userErrorView
                .getVisibility().getStaticExpectation(Timing.IMMEDIATELY, true, false))
                .addInitState(userErrorView.getVisibility(), true);
        createPath(new StateChangeCallback<>(etPwd.getContent(), v -> true, v -> true), pwdErrorView
                .getVisibility().getStaticExpectation(Timing.IMMEDIATELY, true, false))
                .addInitState(pwdErrorView.getVisibility(), true);
        etUser.addPositiveCases("xiaoxiaoming");
        etPwd.addPositiveCases("123456");
        etUser.addNegativeCase("xiao ming", clickLogin, userErrorView.getVisibility().getStaticExpectation(Timing.IMMEDIATELY, true, true)
                .addFollowingExpectation(userErrorView.getText().getStaticExpectation(Timing.IMMEDIATELY, true, "用户名不能含有空格")));
        etPwd.addNegativeCase("123", clickLogin, pwdErrorView.getVisibility().getStaticExpectation(Timing.IMMEDIATELY, true, true)
                .addFollowingExpectation(pwdErrorView.getText().getStaticExpectation(Timing.IMMEDIATELY, true, "密码长度不能小于6个字符")), etUser.getValidity());
        // no connection
        createPath(clickLogin, new Toast(Timing.IMMEDIATELY, getManager(), TOAST_DURATION,
                TOAST_NO_CONNECTION)).addInitState(etUser.getValidity(), true).addInitState(etPwd.getValidity(), true)
                .addInitState(getProperty(NetworkConnectivity.class), false);
        // login
        Timing withinTimeout = new Timing(true, REQUEST_TIMEOUT);
        loginPathHandler.initStateToInvalidPassword(() -> createPath(clickLogin, new Toast(withinTimeout, getManager(), TOAST_DURATION,
                "密码错误")).addInitState(getProperty(NetworkConnectivity.class), true));
        loginPathHandler.initStateToInvalidUser(() -> createPath(clickLogin, new Toast(withinTimeout, getManager(), TOAST_DURATION,
                "用户不存在")).addInitState(getProperty(NetworkConnectivity.class), true));
        loginPathHandler.initStateToSuccessLogin(() -> close(clickLogin, withinTimeout).addInitState(getProperty
                (NetworkConnectivity.class), true));
        loginPathHandler.handleCurrentUserOnSuccessLogin(withinTimeout, e -> createPath(clickLogin, e).addInitState
                (getProperty(NetworkConnectivity.class), true));
    }
}