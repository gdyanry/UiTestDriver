package com.yanry.driver.mobile.sample;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.communicator.ConsoleCommunicator;
import com.yanry.driver.core.model.runtime.Assertion;
import com.yanry.driver.core.model.runtime.GraphWatcher;
import com.yanry.driver.core.model.runtime.MissedPath;
import com.yanry.driver.mobile.sample.model.ConsoleGraphWatcher;
import lib.common.model.log.ConsoleHandler;
import lib.common.model.log.LogLevel;
import lib.common.model.log.Logger;
import lib.common.model.log.SimpleFormatterBuilder;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by rongyu.yan on 2/17/2017.
 */
public class Tester {

    public static void test(boolean verbose, Consumer<Graph> setupGraph) {
        Logger.getDefault().addHandler(new ConsoleHandler(new SimpleFormatterBuilder().level().method().sequenceNumber().build(), verbose ? LogLevel.Verbose : LogLevel.Debug));
        ConsoleCommunicator communicator = new ConsoleCommunicator();
        GraphWatcher watcher = new ConsoleGraphWatcher();
        Graph graph = new Graph();
        graph.registerCommunicator(communicator);
        setupGraph.accept(graph);
        List<Path> options = graph.getConcernedPaths();
        int i = 0;
        for (Path option : options) {
            Logger.getDefault().d("%05d - %s", i++, Graph.getPresentation(option));
        }
        String input = communicator.readLine("请选择需要测试的path：").trim();
        int[] pathIndexes = null;
        if (input.matches("^([1-9]\\d*)|0$")) {
            pathIndexes = new int[]{Integer.parseInt(input)};
        }

        // 打印测试记录
        List<Object> records = graph.traverse(pathIndexes);
        int passCount = 0;
        int failCount = 0;
        int missCount = 0;
        Logger.getDefault().d("-------------------------------------RECORD----------------------------------");
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
                Logger.getDefault().e(Graph.getPresentation(record).toString());
            } else {
                Logger.getDefault().d(Graph.getPresentation(record).toString());
            }
        }
        Logger.getDefault().d("pass/fail/miss: %s/%s/%s", passCount, failCount, missCount);
    }
}
