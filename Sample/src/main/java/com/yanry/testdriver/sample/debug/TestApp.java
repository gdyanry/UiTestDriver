package com.yanry.testdriver.sample.debug;

import com.yanry.testdriver.sample.debug.window.LoginPage;
import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.ProcessState;
import com.yanry.testdriver.ui.mobile.base.runtime.Assertion;
import com.yanry.testdriver.ui.mobile.base.runtime.MissedPath;
import com.yanry.testdriver.ui.mobile.extend.WindowManager;
import com.yanry.testdriver.ui.mobile.extend.communicator.ConsoleCommunicator;
import com.yanry.testdriver.ui.mobile.extend.property.CurrentUser;
import com.yanry.testdriver.ui.mobile.extend.property.LoginState;
import lib.common.util.ConsoleUtil;

import java.util.List;

/**
 * Created by rongyu.yan on 2/17/2017.
 */
public class TestApp {
    public static final int HTTP_TIMEOUT = 10000;
    public static final int TOAST_DURATION = 2000;
    public static final int PLASH_DURATION = 3000;

    public static void main(String[] args) {
        WindowManager manager = new WindowManager(true);
        ConsoleCommunicator communicator = new ConsoleCommunicator();
        manager.registerCommunicator(communicator);
        defineGraph(manager);
        List<Path> options = manager.prepare();
        int i = 0;
        for (Path option : options) {
            System.out.println(String.format("%02d - %s", i++, Util.getPresentation(option)));
        }
        String input = ConsoleUtil.readLine("请选择需要测试的path：").trim();
        int[] pathIndexes = null;
        if (input.matches("^([1-9]\\d*)|0$")) {
            pathIndexes = new int[]{Integer.parseInt(input)};
        }

        // 打印测试记录
        List<Object> records = manager.traverse(pathIndexes);
        int passCount = 0;
        int failCount = 0;
        int missCount = 0;
        System.out.println("-----------------------------------------------------");
        System.out.println("-----------------------------------------------------");
        for (Object record : records) {
            if (record instanceof Assertion) {
                Assertion assertion = (Assertion) record;
                if (assertion.getExpectation().isNeedCheck()) {
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

    public static void defineGraph(WindowManager manager) {
        CurrentUser currentUser = new CurrentUser(manager);
        currentUser.addUserPassword("xiaoming.wang", "aaa111");
        manager.registerProperties(new NetworkState(manager), currentUser, new LoginState(manager, currentUser));
        Util.createPath(manager, new ProcessState(manager).getStateEvent(false, true), new ShowSplash());
        new LoginPage(manager);
    }
}
