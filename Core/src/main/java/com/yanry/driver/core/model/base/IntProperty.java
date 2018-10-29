package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.expectation.Timing;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

public abstract class IntProperty extends Property<Integer> {

    public IntProperty(Graph graph) {
        super(graph);
    }

    public ShiftExpectation getShiftExpectation(Timing timing, boolean needCheck, boolean upward, int step) {
        return new ShiftExpectation(timing, needCheck, upward, step);
    }

    @Override
    protected final ExternalEvent doSelfSwitch(Integer to) {
        int currentValue = getCurrentValue();
        return getGraph().findPathToRoll(exp -> {
            if (exp instanceof ShiftExpectation) {
                ShiftExpectation expectation = (ShiftExpectation) exp;
                if (expectation.getProperty() == this) {
                    return expectation.upward == currentValue < to;
                }
            }
            return false;
        });
    }

    public class ShiftExpectation extends Expectation {
        private boolean upward;
        private int step;

        private ShiftExpectation(Timing timing, boolean needCheck, boolean upward, int step) {
            super(timing, needCheck);
            this.upward = upward;
            this.step = step;
        }

        @Visible
        @EqualsPart
        public IntProperty getProperty() {
            return IntProperty.this;
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

        @Override
        protected boolean doVerify() {
            int oldValue = getCurrentValue();
            int expectedValue = upward ? oldValue + step : oldValue - step;
            handleExpectation(expectedValue, isNeedCheck());
            int actualValue = getCurrentValue();
            if (actualValue != oldValue) {
                getGraph().verifySuperPaths(IntProperty.this, oldValue, actualValue);
            }
            return expectedValue == actualValue;
        }
    }
}
