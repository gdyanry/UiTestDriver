package com.yanry.driver.mobile.property;

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
        getGraph().createPath(new Click(getView()), getStaticExpectation(Timing.IMMEDIATELY, needCheck, true))
                .addContextState(getView(), true)
                .addContextState(this, false);
        // to unchecked
        getGraph().createPath(new Click(getView()), getStaticExpectation(Timing.IMMEDIATELY, needCheck, false))
                .addContextState(getView(), true)
                .addContextState(this, true);
    }

    @Override
    protected Boolean doCheckValue() {
        return getGraph().obtainValue(new BooleanQuery(this));
    }

    @Override
    protected ExternalEvent doSelfSwitch(Boolean to) {
        return null;
    }

    @Override
    protected Stream<Boolean> getValueStream(Set<Boolean> collectedValues) {
        return Stream.of(false, true);
    }
}
