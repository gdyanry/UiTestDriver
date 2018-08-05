package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.runtime.StateToCheck;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.view.View;

public class CheckState extends ViewProperty<Boolean> {
    public CheckState(View view) {
        super(view);
    }

    @Override
    protected Boolean doCheckValue() {
        return getGraph().checkState(new StateToCheck<>(this, false, true));
    }

    @Override
    protected boolean doSelfSwitch(Boolean to) {
        return getView().switchToVisible() || getGraph().performAction(new Click(getView()));
    }
}
