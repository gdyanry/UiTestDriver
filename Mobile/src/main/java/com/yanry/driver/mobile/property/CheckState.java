package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.runtime.fetch.BooleanQuery;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.view.View;

public class CheckState extends ViewProperty<Boolean> {
    public CheckState(View view) {
        super(view);
    }

    public void switchCheckStateOnClick(boolean needCheck) {
        // to checked
        getView().getWindow().createPath(new Click<>(getView()), getStaticExpectation(Timing.IMMEDIATELY, needCheck, true))
                .addInitState(this, false);
        // to unchecked
        getView().getWindow().createPath(new Click<>(getView()), getStaticExpectation(Timing.IMMEDIATELY, needCheck, false))
                .addInitState(this, true);
    }

    @Override
    protected Boolean doCheckValue() {
        return getGraph().obtainValue(new BooleanQuery(this));
    }

    @Override
    protected SwitchResult doSelfSwitch(Boolean to) {
        return SwitchResult.NoAction;
    }
}
