package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.SwitchBySearchProperty;

import java.util.List;
import java.util.function.BiPredicate;

/**
 *  This class represents a transient expectation such as a toast or a loading dialog.
 * Created by rongyu.yan on 3/9/2017.
 */
@Presentable
public abstract class NonPropertyExpectation extends AbstractExpectation {

    public NonPropertyExpectation(Timing timing) {
        super(timing);
    }

    protected abstract Graph getGraph();

    @Override
    protected boolean selfVerify(List<Path> superPathContainer) {
        return getGraph().verifyExpectation(this);
    }

    @Override
    protected boolean isSelfSatisfied(BiPredicate<SwitchBySearchProperty, Object> endStatePredicate) {
        return false;
    }

    @Override
    public boolean ifRecord() {
        return true;
    }
}
