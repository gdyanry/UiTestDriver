package com.yanry.testdriver.sample.debug;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.expectation.TransientExpectation;

/**
 * Created by rongyu.yan on 5/10/2017.
 */
public class ShowSplash extends TransientExpectation {

    public ShowSplash(Graph graph) {
        super(Timing.IMMEDIATELY, graph, TestApp.PLASH_DURATION);
    }
}
