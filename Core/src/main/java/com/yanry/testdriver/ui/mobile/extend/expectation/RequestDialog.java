package com.yanry.testdriver.ui.mobile.extend.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.expectation.TransientExpectation;

/**
 * Created by rongyu.yan on 5/11/2017.
 */
public class RequestDialog extends TransientExpectation {
    public RequestDialog(Timing timing, int duration) {
        super(timing, duration);
    }
}
