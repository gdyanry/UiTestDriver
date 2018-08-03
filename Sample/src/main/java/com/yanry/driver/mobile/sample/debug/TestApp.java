package com.yanry.driver.mobile.sample.debug;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.Path;
import com.yanry.driver.core.model.communicator.ConsoleCommunicator;
import com.yanry.driver.core.model.runtime.Assertion;
import com.yanry.driver.core.model.runtime.GraphWatcher;
import com.yanry.driver.core.model.runtime.MissedPath;
import com.yanry.driver.mobile.sample.debug.window.SetupBox;
import com.yanry.driver.mobile.sample.model.ConsoleGraphWatcher;
import com.yanry.driver.mobile.sample.model.ConsoleLoggable;
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
        ConsoleLoggable loggable = new ConsoleLoggable();
        ConsoleCommunicator communicator = new ConsoleCommunicator();
        GraphWatcher watcher = new ConsoleGraphWatcher();
        Graph graph = new Graph(loggable, watcher);
        graph.registerCommunicator(communicator);
        new SetupBox(graph).setup();
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
}
