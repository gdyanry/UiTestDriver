package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.expectation.SDPropertyExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

public abstract class IntegerProperty extends Property<Integer> {

    public IntegerProperty(StateSpace stateSpace) {
        super(stateSpace);
    }

    public ShiftExpectation getShiftExpectation(Timing timing, boolean needCheck, boolean upward, int step) {
        return new ShiftExpectation(timing, needCheck, upward, step);
    }

    @Override
    protected final ExternalEvent doSelfSwitch(Integer to, ActionFilter actionFilter) {
        Integer currentValue = getCurrentValue();
        if (currentValue == null || to == null) {
            return null;
        }
        return getStateSpace().findPathToRoll(exp -> {
            if (exp instanceof ShiftExpectation) {
                ShiftExpectation expectation = (ShiftExpectation) exp;
                if (expectation.getProperty() == this) {
                    return expectation.upward == currentValue < to;
                }
            }
            return false;
        }, actionFilter);
    }

    public class ShiftExpectation extends SDPropertyExpectation<Integer> {
        private boolean upward;
        private int step;

        private ShiftExpectation(Timing timing, boolean needCheck, boolean upward, int step) {
            super(timing, needCheck, IntegerProperty.this, () -> {
                Integer currentValue = IntegerProperty.this.getCurrentValue();
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
}
