package com.yanry.driver.mobile.sample.login;

import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.core.model.base.TransitionEvent;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.sample.Tester;
import com.yanry.driver.mobile.sample.login.window.AboutPage;
import com.yanry.driver.mobile.sample.login.window.LoginPage;
import com.yanry.driver.mobile.sample.login.window.MainPage;
import com.yanry.driver.mobile.window.Application;

public class LoginTest extends Application {
    public LoginTest(StateSpace stateSpace) {
        super(stateSpace);
        CurrentUser currentUser = new CurrentUser(stateSpace);
        currentUser.addUserPassword("xiaoming.wang", "aaa111");
        currentUser.addUserPassword("rongyu.yan", "88888888");
        NetworkState networkState = new NetworkState(stateSpace);
        stateSpace.createPath(new TransitionEvent<>(getProcessState(), false, true), new ShowSplash(stateSpace));
        LoginPage loginPage = new LoginPage(stateSpace, this, currentUser, networkState);
        MainPage mainPage = new MainPage(stateSpace, this, currentUser);
        AboutPage aboutPage = new AboutPage(stateSpace, this);
        registerWindows(loginPage, mainPage, aboutPage);
    }

    public static void main(String... args) {
        Tester.test(true, graph -> new LoginTest(graph));
    }
}
