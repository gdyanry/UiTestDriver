package com.yanry.testdriver.ui.mobile.model.extend.expectation;

import com.yanry.testdriver.ui.mobile.model.base.Presentable;
import com.yanry.testdriver.ui.mobile.model.base.Timing;
import com.yanry.testdriver.ui.mobile.model.base.TransientExpectation;

/**
 * Created by rongyu.yan on 3/3/2017.
 */
@Presentable
public class GeneralExpectation extends TransientExpectation {
    private Object tag;

    public GeneralExpectation(Timing timing, int duration, Object tag) {
        super(timing, duration);
        this.tag = tag;
    }

    @Presentable
    public Object getTag() {
        return tag;
    }
}
