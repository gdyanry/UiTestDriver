package com.yanry.driver.mobile.expectation;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.expectation.TransientExpectation;
import com.yanry.driver.core.model.runtime.Presentable;

/**
 * Created by rongyu.yan on 3/3/2017.
 */
public class Toast extends TransientExpectation {
    private String message;

    public Toast(Timing timing, Graph graph, int duration, String message) {
        super(timing, graph, duration);
        this.message = message;
    }

    @Presentable
    public String getMessage() {
        return message;
    }
}
