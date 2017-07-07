package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.SearchableSwitchableProperty;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
@Presentable
public abstract class NotPropertyExpectation extends AbstractExpectation {

    public NotPropertyExpectation(Timing timing) {
        super(timing);
    }

    protected abstract Graph getGraph();

    @Override
    protected boolean verify(List<Path> superPathContainer) {
        return getGraph().verifyExpectation(this);
    }

    @Override
    protected boolean selfSwitchTest(BiPredicate<SearchableSwitchableProperty, Object> endStatePredicate) {
        return false;
    }

    @Override
    public boolean ifRecord() {
        return true;
    }
}
