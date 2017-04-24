package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;

import java.util.List;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
@Presentable
public abstract class StatelessExpectation extends AbstractExpectation {

    public StatelessExpectation(Timing timing) {
        super(timing);
    }

    @Override
    public boolean verify(List<Path> superPathContainer) {
        return getGraph().verifyExpectation(this);
    }

    @Override
    public boolean ifRecord() {
        return true;
    }
}
