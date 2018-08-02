package com.yanry.driver.mobile.sample.debug;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.Path;
import com.yanry.driver.core.model.communicator.ConsoleCommunicator;
import com.yanry.driver.core.model.property.CacheProperty;
import com.yanry.driver.core.model.runtime.Assertion;
import com.yanry.driver.core.model.runtime.GraphWatcher;
import com.yanry.driver.core.model.runtime.MissedPath;
import com.yanry.driver.mobile.WindowManager;
import com.yanry.driver.mobile.property.CurrentUser;
import com.yanry.driver.mobile.sample.debug.window.LoginPage;
import com.yanry.driver.mobile.sample.debug.window.MainPage;
import lib.common.util.ConsoleUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by rongyu.yan on 2/17/2017.
 */
public class TestApp {
    public static final int HTTP_TIMEOUT = 10000;
    public static final int TOAST_DURATION = 2000;
    public static final int PLASH_DURATION = 3000;

    public static void main(String[] args) {
        Graph graph = new Graph(true);
        ConsoleCommunicator communicator = new ConsoleCommunicator();
        graph.registerCommunicator(communicator);
        graph.setWatcher(new GraphWatcher() {
            @Override
            public void onStandby(Map<CacheProperty, Object> cacheProperties, Set<Path> unprocessedPaths, Set<Path> successTemp, Set<Path> failedPaths, Path rollingPath) {
                ConsoleUtil.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                ConsoleUtil.debug("rolling path: %s.", Graph.getPresentation(rollingPath));
                ConsoleUtil.debug("unprocessed paths: %s.", unprocessedPaths.size());
                for (CacheProperty property : cacheProperties.keySet()) {
                    ConsoleUtil.debug(">>>>%s - %s", Graph.getPresentation(property), Graph.getPresentation(property.getCurrentValue()));
//                    printWindowVisibility(property);
                }
                ConsoleUtil.debug("success temp:");
                for (Path path : successTemp) {
                    ConsoleUtil.debug("    %s", Graph.getPresentation(path));
                }
                ConsoleUtil.debug("failed paths:");
                for (Path failedPath : failedPaths) {
                    ConsoleUtil.debug("    %s", Graph.getPresentation(failedPath));
                }
                ConsoleUtil.debug("------------------------------------------------------------------------------------------");
            }

            private void printWindowVisibility(CacheProperty property) {
                if (property instanceof WindowManager.Window.PreviousWindow) {
                    WindowManager.Window.PreviousWindow previousWindow = (WindowManager.Window.PreviousWindow) property;
                    WindowManager.Window.VisibilityState visibilityState = previousWindow.getWindow().getVisibility();
                    ConsoleUtil.debug(">>>>%s - %s", Graph.getPresentation(visibilityState), visibilityState.getCurrentValue());
                }
            }
        });
        defineGraph(graph);
        List<Path> options = graph.prepare();
        int i = 0;
        for (Path option : options) {
            System.out.println(String.format("%02d - %s", i++, Graph.getPresentation(option)));
        }
        String input = ConsoleUtil.readLine("请选择需要测试的path：").trim();
        int[] pathIndexes = null;
        if (input.matches("^([1-9]\\d*)|0$")) {
            pathIndexes = new int[]{Integer.parseInt(input)};
        }

        // 打印测试记录
        List<Object> records = graph.traverse(pathIndexes);
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
            System.out.println(Graph.getPresentation(record));
        }
        System.out.printf("pass/fail/miss: %s/%s/%s", passCount, failCount, missCount);
    }

    public static void defineGraph(Graph graph) {
        WindowManager manager = new WindowManager(graph);
        CurrentUser currentUser = new CurrentUser(graph);
        currentUser.addUserPassword("xiaoming.wang", "aaa111");
        graph.addPath(new Path(manager.getProcessState().getStateEvent(false, true), new ShowSplash(graph)));
        LoginPage loginPage = new LoginPage(manager) {
            @Override
            protected CurrentUser getCurrentUser() {
                return null;
            }

            @Override
            protected NetworkState getNetworkState() {
                return null;
            }

            @Override
            protected MainPage getMainPage() {
                return mainPage;
            }
        };
        MainPage mainPage = new MainPage(manager) {
            @Override
            protected CurrentUser getCurrentUser() {
                return null;
            }

            @Override
            protected LoginPage getLoginPage() {
                return loginPage;
            }
        };
    }
}
