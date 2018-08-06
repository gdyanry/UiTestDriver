package com.yanry.driver.core.model.communicator;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.base.Expectation;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.StateToCheck;
import lib.common.util.ConsoleUtil;

/**
 * Created by rongyu.yan on 3/13/2017.
 */
public class ConsoleCommunicator extends SerializedCommunicator {
    private String getInput(int repeat, String type, Object content, Runnable showHint) {
        String prompt = String.format("----%s: %s", type, Graph.getPresentation(content));
        if (repeat == 0) {
            return ConsoleUtil.readLine(prompt);
        } else {
            if (showHint != null) {
                System.out.println("---------------请输入正确的数字--------------");
                showHint.run();
                System.out.println("---------------------------------------------");
            }
            return ConsoleUtil.readLine(prompt);
        }
    }

    @Override
    protected <V> String checkState(int repeat, StateToCheck<V> stateToCheck) {
        return getInput(repeat, "select", stateToCheck, () -> {
            for (int i = 0; i < stateToCheck.getOptions().length; i++) {
                V v = stateToCheck.getOptions()[i];
                System.out.printf("%s - %s%n", i, Graph.getPresentation(v));
            }
        });
    }

    @Override
    public String fetchValue(Property<String> property) {
        return getInput(0, "check", property, null);
    }

    @Override
    protected String performAction(int repeat, ActionEvent actionEvent) {
        getInput(repeat, "perform", actionEvent, null);
        return "1";
    }

    @Override
    protected String verifyExpectation(int repeat, Expectation expectation) {
        return getInput(repeat, "verify", expectation, () -> {
            System.out.println("0 - false");
            System.out.println("1 - true");
            System.out.println("-1 - unknown");
        });
    }
}
