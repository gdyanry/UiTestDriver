package com.yanry.testdriver.ui.mobile.model.test;

import com.yanry.testdriver.ui.mobile.model.base.*;
import com.yanry.testdriver.ui.mobile.model.base.view.View;
import com.yanry.testdriver.ui.mobile.model.base.window.Window;
import com.yanry.testdriver.ui.mobile.model.base.window.WindowManager;
import com.yanry.testdriver.ui.mobile.model.extend.action.Click;
import com.yanry.testdriver.ui.mobile.model.extend.communicator.ConsoleCommunicator;
import com.yanry.testdriver.ui.mobile.model.extend.expectation.GeneralExpectation;
import com.yanry.testdriver.ui.mobile.model.extend.expectation.Toast;
import com.yanry.testdriver.ui.mobile.model.extend.value.EditTextValidity;
import com.yanry.testdriver.ui.mobile.model.extend.view.EditText;

import java.util.List;

/**
 * Created by rongyu.yan on 2/17/2017.
 */
public class TestApp {
    private static final int HTTP_TIMEOUT = 10000;
    private static final int TOAST_DURATION = 2000;

    public static void main(String[] args) {
        Graph graph = new Graph();
        ConsoleCommunicator interactor = new ConsoleCommunicator();
        graph.registerCommunicator(interactor);
        defineGraph(graph);

        // 打印测试记录
        List<Object> records = graph.traverse(null);
        int passCount = 0;
        int failCount = 0;
        int missCount = 0;
        for (Object record : records) {
            if (record instanceof Assertion) {
                Assertion assertion = (Assertion) record;
                if (assertion.getExpectation() instanceof PermanentExpectation) {
                    PermanentExpectation endState = (PermanentExpectation) assertion.getExpectation();
                    if (!endState.getProperty().isNeedVerification()) {
                        continue;
                    }
                }
                if (assertion.isPass()) {
                    passCount++;
                } else {
                    failCount++;
                }
            } else if (record instanceof MissedPath) {
                missCount++;
            }
            System.out.println(Util.getPresentation(record));
        }
        System.out.printf("pass/fail/miss: %s/%s/%s", passCount, failCount, missCount);
    }

    public static void defineGraph(final Graph graph) {
        WindowManager manager = new WindowManager(graph);
        NetworkState networkState = new NetworkState(graph);
        // 启动应用，显示启动页，如果用户未登录则进入登录页，否则进入主页
        ObjectProperty<Boolean> loginState = new LoginState(graph);
        Expectation showSplash = new GeneralExpectation(Timing.IMMEDIATELY, 3000, "出现启动画面");

        new Path(graph, null, graph.getProcessState().getStartProcessEvent(), showSplash);

        Window loginPage = new Window("登录页", graph, manager);
        Timing after3 = new Timing(false, 3000);
        loginPage.popOnStartUp(after3).put(loginState, false);

        Window mainPage = new Window("主页", graph, manager);
        mainPage.popOnStartUp(after3).put(loginState, true);

        // 登录页添加登录按钮
        View btnLogin = new View(loginPage, "登录按钮");
        Click clickLogin = new Click(btnLogin);
        // 登录页添加用户名和密码输入框
        EditText etAccount = new EditText(loginPage, "用户名", null, false, graph) {
            @Override
            protected String getInitContent() {
                return "";
            }
        };
        etAccount.addNegativeTestCase("A lan", "用户名不能包含空格", clickLogin, TOAST_DURATION);
        etAccount.addPositiveTestCase("xiaoming.wang", true);
        etAccount.addPositiveTestCase("daming.wang", false);
        EditText etPassword = new EditText(loginPage, "密码", etAccount, false, graph) {
            @Override
            protected String getInitContent() {
                return "";
            }
        };
        etPassword.addNegativeTestCase("124", "密码不能少于6位", clickLogin, TOAST_DURATION);
        etPassword.addPositiveTestCase("123456", false);
        etPassword.addPositiveTestCase("aaa123", true);

        Timing withinTimeout = new Timing(true, HTTP_TIMEOUT);
        new ForegroundRequest() {
            @Override
            protected void onNoConnection(NetworkState property, Network value, Event event) {
                Path path = new Path(graph, loginPage, event, new Toast(Timing.IMMEDIATELY, TOAST_DURATION, "无网络连接"));
                path.put(property, value);
                etPassword.allClientDependantValidationPass(true, path);
            }

            @Override
            protected void onLoading(NetworkState property, Network value, Event event) {
                Path path = new Path(graph, loginPage, event, new GeneralExpectation(Timing.IMMEDIATELY, HTTP_TIMEOUT, "请求对话框"));
                path.put(property, value);
                etPassword.allClientDependantValidationPass(true, path);
            }

            @Override
            protected void onSuccess(NetworkState property, Network value, Event event) {
                Path loginPath = new Path(graph, loginPage, event, new PermanentExpectation<>(loginState, true, withinTimeout));
                businessSuccess(property, value, loginPath);

                Path popMainPath = loginPage.popWindow(mainPage, event, withinTimeout, true, true);
                businessSuccess(property, value, popMainPath);

                Path invalidAccount = new Path(graph, loginPage, event, new Toast(withinTimeout, TOAST_DURATION, "用户不存在"));
                invalidAccount.put(property, value);
                invalidAccount.put(etAccount.getServerValidity(), EditTextValidity.ClientPass_ServerFail);
                invalidAccount.put(etPassword.getClientValidity(), true);

                Path invalidPassword = new Path(graph, loginPage, event, new Toast(withinTimeout, TOAST_DURATION, "密码错误"));
                invalidPassword.put(property, value);
                invalidPassword.put(etAccount.getServerValidity(), EditTextValidity.ServerPass);
                invalidPassword.put(etPassword.getServerValidity(), EditTextValidity.ClientPass_ServerFail);
            }

            private void businessSuccess(NetworkState property, Network value, Path path) {
                path.put(property, value);
                path.put(etAccount.getServerValidity(), EditTextValidity.ServerPass);
                path.put(etPassword.getServerValidity(), EditTextValidity.ServerPass);
            }

            @Override
            protected void onError(NetworkState property, Network value, Event event) {
                Path path = new Path(graph, loginPage, event, new Toast(withinTimeout, TOAST_DURATION, "网络错误"));
                path.put(property, value);
                etPassword.allClientDependantValidationPass(true, path);
            }
        }.send(networkState, clickLogin);

        // 主页添加退出登录按钮
        View btnLogout = new View(mainPage, "退出登录按钮");
        Click clickLogout = new Click(btnLogout);
        mainPage.popWindow(loginPage, clickLogout, Timing.IMMEDIATELY, true, true);
        new Path(graph, mainPage, clickLogout, new PermanentExpectation<>(loginState, false, Timing.IMMEDIATELY));

        // 关于
        Window aboutPage = new Window("关于页面", graph, manager);
        View btnAbout = new View(mainPage, "关于按钮");
        Click clickAbout = new Click(btnAbout);
        mainPage.popWindow(aboutPage, clickAbout, Timing.IMMEDIATELY, false, false);
        View btnClose = new View(aboutPage, "关闭按钮");
        Click clickClose = new Click(btnClose);
        aboutPage.close(clickClose, Timing.IMMEDIATELY);
    }
}
