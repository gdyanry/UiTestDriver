package com.yanry.testdriver.sample.debug;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.*;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.StateProperty;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.runtime.Assertion;
import com.yanry.testdriver.ui.mobile.base.runtime.MissedPath;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.communicator.ConsoleCommunicator;
import com.yanry.testdriver.ui.mobile.extend.expectation.GeneralExpectation;
import com.yanry.testdriver.ui.mobile.extend.expectation.Toast;
import com.yanry.testdriver.ui.mobile.extend.value.EditTextValidity;
import com.yanry.testdriver.ui.mobile.extend.view.EditText;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByDesc;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByText;
import com.yanry.testdriver.ui.mobile.extend.window.Window;
import com.yanry.testdriver.ui.mobile.extend.window.WindowManager;

import java.util.List;

/**
 * Created by rongyu.yan on 2/17/2017.
 */
public class TestApp {
    private static final int HTTP_TIMEOUT = 10000;
    private static final int TOAST_DURATION = 2000;

    public static void main(String[] args) {
        Graph graph = new Graph(false);
        WindowManager manager = new WindowManager(graph);
        ConsoleCommunicator interactor = new ConsoleCommunicator();
        graph.registerCommunicator(interactor);
        defineGraph(graph, manager);

        // 打印测试记录
        List<Object> records = graph.traverse(null);
        int passCount = 0;
        int failCount = 0;
        int missCount = 0;
        System.out.println("-----------------------------------------------------");
        System.out.println("-----------------------------------------------------");
        for (Object record : records) {
            if (record instanceof Assertion) {
                Assertion assertion = (Assertion) record;
                if (assertion.getExpectation().ifRecord()) {
                if (assertion.isPass()) {
                    passCount++;
                } else {
                    failCount++;
                }
                }
            } else if (record instanceof MissedPath) {
                missCount++;
            }
            System.out.println(Util.getPresentation(record));
        }
        System.out.printf("pass/fail/miss: %s/%s/%s", passCount, failCount, missCount);
    }

    public static void defineGraph(Graph graph, WindowManager manager) {

        NetworkState networkState = new NetworkState(graph);
        // 启动应用，显示启动页，如果用户未登录则进入登录页，否则进入主页
        StateProperty<Boolean> loginState = new LoginState(graph);
        Expectation showSplash = new GeneralExpectation(graph, "出现启动画面", Timing.IMMEDIATELY, 3000);

        Util.createPath(graph, null, graph.getProcessState().getStartProcessEvent(), showSplash);

        Window loginPage = new Window("登录页", graph, manager);
        Timing after3 = new Timing(false, 3000);
        loginPage.showOnStartUp(after3).put(loginState, false);

        Window mainPage = new Window("主页", graph, manager);
        mainPage.showOnStartUp(after3).put(loginState, true);

        // 登录页添加登录按钮
        View btnLogin = new View(loginPage, new ByText("登录"));
        Click clickLogin = new Click(btnLogin);
        // 登录页添加用户名和密码输入框
        EditText etAccount = new EditText(graph, loginPage, new ByDesc("用户名"), null, false) {
            @Override
            protected String getInitContent() {
                return "";
            }
        };
        etAccount.addNegativeTestCase("A lan", "用户名不能包含空格", clickLogin, TOAST_DURATION);
        etAccount.addPositiveTestCase("xiaoming.wang", true);
        etAccount.addPositiveTestCase("daming.wang", false);
        EditText etPassword = new EditText(graph, loginPage, new ByDesc("密码"), etAccount, false) {
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
                Path path = Util.createPath(graph, loginPage, event, new Toast(graph, "无网络连接", Timing.IMMEDIATELY,
                        TOAST_DURATION));
                path.put(property, value);
                etPassword.allClientDependantValidationPass(true, path);
            }

            @Override
            protected void onLoading(NetworkState property, Network value, Event event) {
                Path path = Util.createPath(graph, loginPage, event, new GeneralExpectation(graph, "请求对话框", Timing
                        .IMMEDIATELY, HTTP_TIMEOUT));
                path.put(property, value);
                etPassword.allClientDependantValidationPass(true, path);
            }

            @Override
            protected void onSuccess(NetworkState property, Network value, Event event) {
                Path loginPath = Util.createPath(graph, loginPage, event, loginState.getExpectation(withinTimeout,
                        true));
                businessSuccess(property, value, loginPath);

                Path popMainPath = loginPage.popWindow(mainPage, event, withinTimeout, true, true);
                businessSuccess(property, value, popMainPath);

                Util.createPath(graph, loginPage, event, new Toast(graph, "用户不存在",
                        withinTimeout, TOAST_DURATION)).addInitState(property, value).addInitState(etAccount
                        .getServerValidity(), EditTextValidity.ClientPass_ServerFail).addInitState(etPassword.getClientValidity()
                        , true);

                Util.createPath(graph, loginPage, event, new Toast(graph, "密码错误",
                        withinTimeout, TOAST_DURATION)).addInitState(property, value).addInitState(etAccount.getServerValidity(),
                        EditTextValidity.ServerPass).addInitState(etPassword.getServerValidity(), EditTextValidity
                        .ClientPass_ServerFail);
            }

            private void businessSuccess(NetworkState property, Network value, Path path) {
                path.addInitState(property, value).addInitState(etAccount.getServerValidity(), EditTextValidity
                        .ServerPass).addInitState(etPassword.getServerValidity(), EditTextValidity.ServerPass);
            }

            @Override
            protected void onError(NetworkState property, Network value, Event event) {
                etPassword.allClientDependantValidationPass(true, Util.createPath(graph, loginPage, event, new Toast
                        (graph, "网络错误", withinTimeout, TOAST_DURATION)).addInitState(property, value));
            }
        }.send(networkState, clickLogin);

        // 主页添加退出登录按钮
        Click clickLogout = new Click(new View(mainPage, new ByText("退出登录")));
        mainPage.popWindow(loginPage, clickLogout, Timing.IMMEDIATELY, true, true);
        Util.createPath(graph, mainPage, clickLogout, loginState.getExpectation(Timing.IMMEDIATELY, false));

        // 关于
        Window aboutPage = new Window("关于页面", graph, manager);
        View btnAbout = new View(mainPage, new ByText("关于"));
        Click clickAbout = new Click(btnAbout);
        mainPage.popWindow(aboutPage, clickAbout, Timing.IMMEDIATELY, false, false);
        View btnClose = new View(aboutPage, new ByText("关闭"));
        Click clickClose = new Click(btnClose);
        aboutPage.close(clickClose, Timing.IMMEDIATELY);
    }
}
