package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Expectation;

public class ExpectationEvent extends ActionEvent {
    private Expectation expectation;

    public ExpectationEvent(Expectation expectation) {
        this.expectation = expectation;
    }

    public Expectation getExpectation() {
        return expectation;
    }
}
