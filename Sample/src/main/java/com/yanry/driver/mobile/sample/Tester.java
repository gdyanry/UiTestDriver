package com.yanry.driver.mobile.sample;

import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.core.model.runtime.Assertion;
import com.yanry.driver.core.model.runtime.MissedPath;
import com.yanry.driver.core.model.runtime.Watcher;
import com.yanry.driver.core.model.runtime.communicator.ConsoleCommunicator;
import com.yanry.driver.mobile.sample.model.ConsoleWatcher;
import yanry.lib.java.model.log.ConsoleHandler;
import yanry.lib.java.model.log.LogLevel;
import yanry.lib.java.model.log.Logger;
import yanry.lib.java.model.log.SimpleFormatter;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by rongyu.yan on 2/17/2017.
 */
public class Tester {

    public static void test(boolean verbose, Consumer<StateSpace> setupGraph) {
        Logger.getDefault().addHandler(new ConsoleHandler(new SimpleFormatter().method(2).sequenceNumber(), verbose ? LogLevel.Verbose : LogLevel.Debug));
        ConsoleCommunicator communicator = new ConsoleCommunicator();
        Watcher watcher = new ConsoleWatcher();
        StateSpace stateSpace = new StateSpace();
        stateSpace.setCommunicator(communicator);
        stateSpace.setWatcher(watcher);
        setupGraph.accept(stateSpace);
        List<Path> options = stateSpace.getConcernedPaths();
        int i = 0;
        for (Path option : options) {
            System.out.println(String.format("%05d - %s", i++, option));
        }
        String input = communicator.readLine("请选择需要测试的path：").trim();
        int[] pathIndexes = null;
        if (input.matches("^([1-9]\\d*)|0$")) {
            pathIndexes = new int[]{Integer.parseInt(input)};
        }

        // 打印测试记录
        int passCount = 0;
        int failCount = 0;
        int missCount = 0;
        System.out.println("-------------------------------------RECORD----------------------------------");
        int n = 0;
        for (Object record : stateSpace.traverse(pathIndexes)) {
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
                System.err.println(String.format("%s: %s", n++, record));
            } else {
                System.out.println(String.format("%s: %s", n++, record));
            }
        }
        Logger.getDefault().d("pass/fail/miss: %s/%s/%s", passCount, failCount, missCount);
        stateSpace.release();
    }
}
