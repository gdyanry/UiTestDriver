package com.yanry.testdriver.ui.mobile.extend.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.expectation.TransientExpectation;

/**
 * Created by rongyu.yan on 3/3/2017.
 */
public class Toast extends TransientExpectation {
    private String message;

    public Toast(Timing timing, int duration, String message) {
        super(timing, duration);
        this.message = message;
    }

    @Presentable
    public String getMessage() {
        return message;
    }
}
