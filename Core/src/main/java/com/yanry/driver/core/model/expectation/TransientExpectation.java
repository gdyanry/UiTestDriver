package com.yanry.driver.core.model.expectation;

import com.yanry.driver.core.model.base.NonPropertyExpectation;
import com.yanry.driver.core.model.base.StateSpace;
import yanry.lib.java.util.object.Visible;

/**
 * Created by rongyu.yan on 4/24/2017.
 */
public class TransientExpectation extends NonPropertyExpectation {
    private int duration;

    public TransientExpectation(Timing timing, StateSpace stateSpace, int duration) {
        super(timing, stateSpace);
        this.duration = duration;
    }

    @Visible
    public int getDuration() {
        return duration;
    }
}
