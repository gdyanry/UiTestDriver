package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.expectation.Timing;

/**
 * This class represents a transient expectation such as a toast or a loading dialog.
 * Created by rongyu.yan on 3/9/2017.
 */
public abstract class NonPropertyExpectation extends Expectation {
    private StateSpace stateSpace;

    public NonPropertyExpectation(Timing timing, StateSpace stateSpace) {
        super(timing, true);
        this.stateSpace = stateSpace;
    }

    @Override
    protected final boolean doVerify() {
        return stateSpace.verifyExpectation(this);
    }
}
