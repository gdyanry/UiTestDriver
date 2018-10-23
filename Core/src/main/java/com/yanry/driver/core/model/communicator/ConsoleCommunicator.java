package com.yanry.driver.core.model.communicator;

import com.yanry.driver.core.model.base.Expectation;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.runtime.fetch.Obtainable;

import java.util.Scanner;

/**
 * Created by rongyu.yan on 3/13/2017.
 */
public class ConsoleCommunicator extends SerializedCommunicator {
    private Scanner scanner;

    public ConsoleCommunicator() {
        scanner = new Scanner(System.in);
    }

    public String readLine(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }

    private String getInput(int repeat, String type, Object content, Runnable showHint) {
        String prompt = String.format("----%s: %s", type, content);
        if (repeat == 0) {
            return readLine(prompt);
        } else {
            if (showHint != null) {
                System.out.println("---------------请输入正确的数字--------------");
                showHint.run();
                System.out.println("---------------------------------------------");
            }
            return readLine(prompt);
        }
    }

    @Override
    protected <V> String checkState(int repeat, Obtainable<V> stateToCheck) {
        return getInput(repeat, "obtain", stateToCheck, null);
    }

    @Override
    protected String performAction(int repeat, ExternalEvent externalEvent) {
        getInput(repeat, "perform", externalEvent, null);
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
