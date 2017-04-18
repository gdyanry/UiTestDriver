package com.yanry.testdriver.ui.mobile.model.extend.communicator;

import com.yanry.testdriver.ui.mobile.model.base.ActionEvent;
import com.yanry.testdriver.ui.mobile.model.base.StateToCheck;
import com.yanry.testdriver.ui.mobile.model.base.TransientExpectation;
import com.yanry.testdriver.ui.mobile.model.base.Util;
import lib.common.util.ConsoleUtil;

/**
 * Created by rongyu.yan on 3/13/2017.
 */
public class ConsoleCommunicator extends SerializedCommunicator {
    private String getInput(int repeat, String type, Object content, Runnable showHint) {
        String prompt = String.format("%s: %s", type, Util.getPresentation(content));
        if (repeat == 0) {
            return ConsoleUtil.readLine(prompt);
        } else {
            System.out.println("---------------请输入正确的数字--------------");
            showHint.run();
            System.out.println("---------------------------------------------");
            return ConsoleUtil.readLine(prompt);
        }
    }

    @Override
    protected <V> String checkState(int repeat, StateToCheck<V> stateToCheck) {
        return getInput(repeat, "check", stateToCheck, () -> {
            for (int i = 0; i < stateToCheck.getOptions().length; i++) {
                V v = stateToCheck.getOptions()[i];
                System.out.printf("%s - %s%n", i, v);
            }
        });
    }

    @Override
    protected String performAction(int repeat, ActionEvent actionEvent) {
        getInput(repeat, "perform", actionEvent, null);
        return "1";
    }

    @Override
    protected String verifyExpectation(int repeat, TransientExpectation expectation) {
        return getInput(repeat, "verify", expectation, () -> {
            System.out.println("0 - false");
            System.out.println("1 - true");
            System.out.println("-1 - unknown");
        });
    }
}
