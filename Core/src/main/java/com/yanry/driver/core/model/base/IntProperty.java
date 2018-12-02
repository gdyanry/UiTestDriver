package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.expectation.ShiftExpectation;
import com.yanry.driver.core.model.expectation.Timing;

public abstract class IntProperty extends Property<Integer> {

    public IntProperty(Graph graph) {
        super(graph);
    }

    public ShiftExpectation getShiftExpectation(Timing timing, boolean needCheck, boolean upward, int step) {
        return new ShiftExpectation(timing, needCheck, this, upward, step);
    }

    @Override
    protected final ExternalEvent doSelfSwitch(Integer to) {
        Integer currentValue = getCurrentValue();
        if (currentValue == null || to == null) {
            return null;
        }
        return getGraph().findPathToRoll(exp -> {
            if (exp instanceof ShiftExpectation) {
                ShiftExpectation expectation = (ShiftExpectation) exp;
                if (expectation.getProperty() == this) {
                    return expectation.isUpward() == currentValue < to;
                }
            }
            return false;
        });
    }
}
