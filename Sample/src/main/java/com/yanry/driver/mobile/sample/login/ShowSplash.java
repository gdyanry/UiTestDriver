package com.yanry.driver.mobile.sample.login;

import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.expectation.TransientExpectation;

/**
 * Created by rongyu.yan on 5/10/2017.
 */
public class ShowSplash extends TransientExpectation {

    public ShowSplash(StateSpace stateSpace) {
        super(Timing.IMMEDIATELY, stateSpace, Const.PLASH_DURATION);
    }
}
