package com.yanry.driver.core.model.expectation;

import com.yanry.driver.core.model.base.IntProperty;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

/**
 * @Author: yanry
 * @Date: 2018/12/2 8:37
 */
public class ShiftExpectation extends SDPropertyExpectation<Integer> {
    private boolean upward;
    private int step;

    public ShiftExpectation(Timing timing, boolean needCheck, IntProperty property, boolean upward, int step) {
        super(timing, needCheck, property, () -> {
            Integer currentValue = property.getCurrentValue();
            if (currentValue != null) {
                return upward ? currentValue + step : currentValue - step;
            }
            return null;
        });
        this.upward = upward;
        this.step = step;
    }

    @Visible
    @EqualsPart
    public boolean isUpward() {
        return upward;
    }

    @Visible
    @EqualsPart
    public int getStep() {
        return step;
    }
}
