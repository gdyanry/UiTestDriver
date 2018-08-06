package com.yanry.driver.mobile.sample.demo;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.expectation.TransientExpectation;

/**
 * Created by rongyu.yan on 5/10/2017.
 */
public class ShowSplash extends TransientExpectation {

    public ShowSplash(Graph graph) {
        super(Timing.IMMEDIATELY, graph, TestApp.PLASH_DURATION);
    }
}
