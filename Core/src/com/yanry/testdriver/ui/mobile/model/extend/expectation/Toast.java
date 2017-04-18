package com.yanry.testdriver.ui.mobile.model.extend.expectation;

import com.yanry.testdriver.ui.mobile.model.base.Presentable;
import com.yanry.testdriver.ui.mobile.model.base.Timing;
import com.yanry.testdriver.ui.mobile.model.base.TransientExpectation;

/**
 * Created by rongyu.yan on 3/3/2017.
 */
@Presentable
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
