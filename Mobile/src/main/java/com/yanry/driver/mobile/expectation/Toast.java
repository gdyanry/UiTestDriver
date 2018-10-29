package com.yanry.driver.mobile.expectation;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.expectation.TransientExpectation;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

/**
 * Created by rongyu.yan on 3/3/2017.
 */
public class Toast extends TransientExpectation {
    private String message;

    public Toast(Timing timing, Graph graph, int duration, String message) {
        super(timing, graph, duration);
        this.message = message;
    }

    @Visible
    @EqualsPart
    public String getMessage() {
        return message;
    }
}
