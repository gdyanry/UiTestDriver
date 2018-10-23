package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Expectation;
import com.yanry.driver.core.model.base.ExternalEvent;

public class ExpectationEvent extends ExternalEvent {
    private Expectation expectation;

    public ExpectationEvent(Expectation expectation) {
        this.expectation = expectation;
    }

    public Expectation getExpectation() {
        return expectation;
    }
}
