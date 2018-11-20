package com.yanry.driver.mobile.sample.login;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.TransitionEvent;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.sample.Tester;
import com.yanry.driver.mobile.sample.login.window.AboutPage;
import com.yanry.driver.mobile.sample.login.window.LoginPage;
import com.yanry.driver.mobile.sample.login.window.MainPage;
import com.yanry.driver.mobile.window.Application;

public class LoginTest extends Application {
    public LoginTest(Graph graph) {
        super(graph);
        CurrentUser currentUser = new CurrentUser(graph);
        currentUser.addUserPassword("xiaoming.wang", "aaa111");
        currentUser.addUserPassword("rongyu.yan", "88888888");
        NetworkState networkState = new NetworkState(graph);
        graph.createPath(new TransitionEvent<>(getProcessState(), false, true), new ShowSplash(graph));
        LoginPage loginPage = new LoginPage(graph, this, currentUser, networkState);
        MainPage mainPage = new MainPage(graph, this, currentUser);
        AboutPage aboutPage = new AboutPage(graph, this);
        registerWindows(loginPage, mainPage, aboutPage);
    }

    public static void main(String... args) {
        Tester.test(true, graph -> new LoginTest(graph));
    }
}
