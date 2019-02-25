package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.ActionFilter;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.runtime.fetch.BooleanQuery;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.view.View;

import java.util.Set;
import java.util.stream.Stream;

public class CheckState extends ViewProperty<Boolean> {
    public CheckState(View view) {
        super(view);
    }

    public void switchCheckStateOnClick(boolean needCheck) {
        // to checked
        getStateSpace().createPath(new Click(getView()), getStaticExpectation(Timing.IMMEDIATELY, needCheck, true))
                .addContextValue(getView(), true)
                .addContextValue(this, false);
        // to unchecked
        getStateSpace().createPath(new Click(getView()), getStaticExpectation(Timing.IMMEDIATELY, needCheck, false))
                .addContextValue(getView(), true)
                .addContextValue(this, true);
    }

    @Override
    protected Boolean checkValue(Boolean expected) {
        return getStateSpace().obtainValue(new BooleanQuery(this), expected);
    }

    @Override
    protected ExternalEvent doSelfSwitch(Boolean to, ActionFilter actionFilter) {
        return null;
    }

    @Override
    protected Stream<Boolean> getValueStream(Set<Boolean> collectedValues) {
        return Stream.of(false, true);
    }
}
