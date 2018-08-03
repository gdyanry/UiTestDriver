package com.yanry.driver.mobile.sample.debug.window;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.Path;
import com.yanry.driver.mobile.WindowManager;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.sample.debug.NetworkState;
import com.yanry.driver.mobile.sample.debug.ShowSplash;

public class SetupBox extends WindowManager {
    private LoginPage loginPage;
    private MainPage mainPage;
    private AboutPage aboutPage;

    public SetupBox(Graph graph) {
        super(graph);
        CurrentUser currentUser = new CurrentUser(graph);
        currentUser.addUserPassword("xiaoming.wang", "aaa111");
        NetworkState networkState = new NetworkState(graph);
        graph.addPath(new Path(getProcessState().getStateEvent(false, true), new ShowSplash(graph)));
        loginPage = new LoginPage(this) {
            @Override
            protected CurrentUser getCurrentUser() {
                return currentUser;
            }

            @Override
            protected NetworkState getNetworkState() {
                return networkState;
            }

            @Override
            protected MainPage getMainPage() {
                return mainPage;
            }
        };
        mainPage = new MainPage(this) {
            @Override
            protected CurrentUser getCurrentUser() {
                return currentUser;
            }

            @Override
            protected LoginPage getLoginPage() {
                return loginPage;
            }

            @Override
            protected AboutPage getAboutPage() {
                return aboutPage;
            }
        };
        aboutPage = new AboutPage(this);
    }
}
