package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Expectation;

public class ExpectationEvent extends ActionEvent {
    private ActionEvent action;
    private Expectation expectation;

    public ExpectationEvent(ActionEvent action, Expectation expectation) {
        this.action = action;
        this.expectation = expectation;
    }

    public ActionEvent getAction() {
        return action;
    }

    public Expectation getExpectation() {
        return expectation;
    }
}
