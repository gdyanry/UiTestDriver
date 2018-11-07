package com.yanry.driver.mobile.sample.login;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.TransitionEvent;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.property.LoginState;
import com.yanry.driver.mobile.sample.Tester;
import com.yanry.driver.mobile.sample.login.window.AboutPage;
import com.yanry.driver.mobile.sample.login.window.LoginPage;
import com.yanry.driver.mobile.sample.login.window.MainPage;
import com.yanry.driver.mobile.window.WindowManager;

public class LoginTest extends WindowManager {
    public LoginTest(Graph graph) {
        super(graph);
        CurrentUser currentUser = new CurrentUser(graph);
        currentUser.addUserPassword("xiaoming.wang", "aaa111");
        NetworkState networkState = new NetworkState(graph);
        LoginState loginState = new LoginState(graph, currentUser);
        graph.addPath(new Path(new TransitionEvent<>(getProcessState(), false, true), new ShowSplash(graph)));
        LoginPage loginPage = new LoginPage(graph, this, currentUser, loginState, networkState);
        MainPage mainPage = new MainPage(graph, this, currentUser, loginState);
        AboutPage aboutPage = new AboutPage(graph, this);
        addWindow(loginPage, mainPage, aboutPage);
    }

    public static void main(String... args) {
        Tester.test(true, graph -> new LoginTest(graph));
    }
}
