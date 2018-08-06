package com.yanry.driver.mobile.sample.reservation.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.event.StateChangeCallback;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.model.LoginPathHandler;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.expectation.Toast;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.property.Text;
import com.yanry.driver.mobile.property.TextValidity;
import com.yanry.driver.mobile.sample.reservation.property.NetworkConnectivity;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByDesc;
import com.yanry.driver.mobile.view.selector.ByText;

import static com.yanry.driver.mobile.sample.reservation.server.Config.*;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class Login extends Window {
    public static String ET_USER;
    public static String ET_PWD;
    public static String USER_VALIDATION;
    public static String PWD_VALIDATION;

    public Login(WindowManager manager) {
        super(manager);
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        View etUser = new View(graph, this, new ByDesc(ET_USER));
        Text txtUser = new Text(etUser);
        TextValidity userValidity = new TextValidity(etUser, txtUser);
        View etPwd = new View(graph, this, new ByDesc(ET_PWD));
        Text txtPwd = new Text(etPwd);
        TextValidity pwdValidity = new TextValidity(etPwd, txtPwd);
        LoginPathHandler loginPathHandler = new LoginPathHandler(new CurrentUser(graph), userValidity, pwdValidity);
        Click clickLogin = new Click(new View(graph, this, new ByText("登录")));
        View userErrorView = new View(graph, this, new ByDesc(USER_VALIDATION));
        View pwdErrorView = new View(graph, this, new ByDesc(PWD_VALIDATION));
        Text txtUserError = new Text(userErrorView);
        Text txtPwdError = new Text(pwdErrorView);


        createPath(getCreateEvent(), txtUser.getStaticExpectation(Timing.IMMEDIATELY, true, ""));
        createPath(getCreateEvent(), txtPwd.getStaticExpectation(Timing.IMMEDIATELY, true, ""));
        userValidity.addNegativeCase("", clickLogin, userErrorView.getIndependentVisibility().getStaticExpectation(Timing.IMMEDIATELY, true, true)
                .addFollowingExpectation(txtUserError.getStaticExpectation(Timing.IMMEDIATELY, true, "用户名不能为空")));
        pwdValidity.addNegativeCase("", clickLogin, pwdErrorView.getIndependentVisibility().getStaticExpectation(Timing.IMMEDIATELY, true, true)
                .addFollowingExpectation(txtPwdError.getStaticExpectation(Timing.IMMEDIATELY, true, "密码不能为空")), userValidity);
        // 输入内容时隐藏错误提示视图
        createPath(new StateChangeCallback<>(txtUser, v -> true, v -> true), userErrorView.getIndependentVisibility().getStaticExpectation(Timing.IMMEDIATELY, true, false))
                .addInitState(userErrorView.getIndependentVisibility(), true);
        createPath(new StateChangeCallback<>(txtPwd, v -> true, v -> true), pwdErrorView.getIndependentVisibility().getStaticExpectation(Timing.IMMEDIATELY, true, false))
                .addInitState(pwdErrorView.getIndependentVisibility(), true);
        userValidity.addPositiveCases("xiaoxiaoming");
        pwdValidity.addPositiveCases("123456");
        userValidity.addNegativeCase("xiao ming", clickLogin, userErrorView.getIndependentVisibility().getStaticExpectation(Timing.IMMEDIATELY, true, true)
                .addFollowingExpectation(txtUserError.getStaticExpectation(Timing.IMMEDIATELY, true, "用户名不能含有空格")));
        pwdValidity.addNegativeCase("123", clickLogin, pwdErrorView.getIndependentVisibility().getStaticExpectation(Timing.IMMEDIATELY, true, true)
                .addFollowingExpectation(txtPwdError.getStaticExpectation(Timing.IMMEDIATELY, true, "密码长度不能小于6个字符")), userValidity);
        // no connection
        NetworkConnectivity connectivity = new NetworkConnectivity(graph);
        createPath(clickLogin, new Toast(Timing.IMMEDIATELY, graph, TOAST_DURATION, TOAST_NO_CONNECTION))
                .addInitState(userValidity, true)
                .addInitState(pwdValidity, true)
                .addInitState(connectivity, false);
        // login
        Timing withinTimeout = new Timing(true, REQUEST_TIMEOUT);
        loginPathHandler.initStateToInvalidPassword(() -> createPath(clickLogin, new Toast(withinTimeout, graph, TOAST_DURATION, "密码错误"))
                .addInitState(connectivity, true));
        loginPathHandler.initStateToInvalidUser(() -> createPath(clickLogin, new Toast(withinTimeout, graph, TOAST_DURATION, "用户不存在"))
                .addInitState(connectivity, true));
        loginPathHandler.initStateToSuccessLogin(() -> close(clickLogin, withinTimeout)
                .addInitState(connectivity, true));
        loginPathHandler.handleCurrentUserOnSuccessLogin(withinTimeout, e -> createPath(clickLogin, e)
                .addInitState(connectivity, true));
    }
}
