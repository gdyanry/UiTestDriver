package com.yanry.driver.core.model.runtime.record;

import com.yanry.driver.core.model.base.Expectation;
import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.HandyObject;
import yanry.lib.java.util.object.Visible;

public class VerificationRecord extends HandyObject implements CommunicateRecord {
    private Expectation expectation;
    private Expectation.VerifyResult result;

    public VerificationRecord(Expectation expectation, Expectation.VerifyResult result) {
        this.expectation = expectation;
        this.result = result;
    }

    @EqualsPart
    @Visible
    public Expectation getExpectation() {
        return expectation;
    }

    @EqualsPart
    @Visible
    public Expectation.VerifyResult getResult() {
        return result;
    }
}
