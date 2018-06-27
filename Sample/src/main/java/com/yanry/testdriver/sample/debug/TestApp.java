package com.yanry.testdriver.sample.debug;

import com.yanry.testdriver.sample.debug.window.AboutPage;
import com.yanry.testdriver.sample.debug.window.LoginPage;
import com.yanry.testdriver.sample.debug.window.MainPage;
import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.runtime.Assertion;
import com.yanry.testdriver.ui.mobile.base.runtime.MissedPath;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.communicator.ConsoleCommunicator;
import com.yanry.testdriver.ui.mobile.extend.property.CurrentUser;
import com.yanry.testdriver.ui.mobile.extend.property.LoginState;

import java.util.List;

/**
 * Created by rongyu.yan on 2/17/2017.
 */
public class TestApp {
    public static final int HTTP_TIMEOUT = 10000;
    public static final int TOAST_DURATION = 2000;
    public static final int PLASH_DURATION = 3000;

    public static void main(String[] args) {
        TestManager manager = new TestManager(true);
        ConsoleCommunicator communicator = new ConsoleCommunicator();
        manager.registerCommunicator(communicator);
        defineGraph(manager);

        // 打印测试记录
        List<Object> records = manager.traverse(null);
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

    public static void defineGraph(TestManager manager) {
        CurrentUser currentUser = new CurrentUser();
        currentUser.addUserPassword("xiaoming.wang", "aaa111");
        manager.registerProperties(new NetworkState(), currentUser, new LoginState(currentUser));
        Util.createPath(manager, manager.getProcessState().getStateEvent(false, true), new ShowSplash());
        manager.registerWindows(new LoginPage(manager), new MainPage(manager), new AboutPage(manager));
    }
}
