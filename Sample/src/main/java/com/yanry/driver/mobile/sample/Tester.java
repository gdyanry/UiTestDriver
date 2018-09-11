package com.yanry.driver.mobile.sample;

import com.yanry.driver.core.Utils;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.communicator.ConsoleCommunicator;
import com.yanry.driver.core.model.runtime.Assertion;
import com.yanry.driver.core.model.runtime.GraphWatcher;
import com.yanry.driver.core.model.runtime.MissedPath;
import com.yanry.driver.mobile.sample.model.ConsoleGraphWatcher;
import com.yanry.driver.mobile.sample.model.ConsoleLoggable;
import lib.common.util.ConsoleUtil;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by rongyu.yan on 2/17/2017.
 */
public class Tester {

    public static void test(Consumer<Graph> setupGraph) {
        ConsoleLoggable loggable = new ConsoleLoggable();
        ConsoleCommunicator communicator = new ConsoleCommunicator();
        GraphWatcher watcher = new ConsoleGraphWatcher();
        Graph graph = new Graph(loggable, watcher);
        graph.registerCommunicator(communicator);
        setupGraph.accept(graph);
        List<Path> options = graph.prepare();
        int i = 0;
        for (Path option : options) {
            System.out.println(String.format("%05d - %s", i++, Utils.getPresentation(option)));
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
        System.out.println("-------------------------------------RECORD----------------------------------");
        for (Object record : records) {
            boolean fail = false;
            if (record instanceof Assertion) {
                Assertion assertion = (Assertion) record;
                if (assertion.getExpectation().isNeedCheck()) {
                    if (assertion.isPass()) {
                        passCount++;
                    } else {
                        failCount++;
                        fail = true;
                    }
                }
            } else if (record instanceof MissedPath) {
                fail = true;
                missCount++;
            }
            if (fail) {
                System.err.println(Utils.getPresentation(record));
            } else {
                System.out.println(Utils.getPresentation(record));
            }
        }
        System.out.printf("pass/fail/miss: %s/%s/%s", passCount, failCount, missCount);
    }
}