package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Expectation;
import com.yanry.driver.core.model.base.ExternalEvent;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

public class ExpectationEvent extends ExternalEvent {
    private Expectation expectation;

    public ExpectationEvent(Expectation expectation) {
        this.expectation = expectation;
    }

    @Visible
    @EqualsPart
    public Expectation getExpectation() {
        return expectation;
    }
}
