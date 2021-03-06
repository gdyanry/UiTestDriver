package com.yanry.driver.mobile.expectation;

import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.expectation.TransientExpectation;

/**
 * Created by rongyu.yan on 5/11/2017.
 */
public class RequestDialog extends TransientExpectation {
    public RequestDialog(Timing timing, StateSpace stateSpace, int duration) {
        super(timing, stateSpace, duration);
    }
}
