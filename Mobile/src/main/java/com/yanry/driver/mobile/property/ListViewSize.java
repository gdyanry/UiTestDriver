package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.runtime.StateToCheck;
import com.yanry.driver.mobile.view.View;

public class ListViewSize extends ViewProperty<Integer> {

    public ListViewSize(View view) {
        super(view);
    }

    @Override
    protected Integer doCheckValue() {
        return getGraph().checkState(new StateToCheck<>(this));
    }

    @Override
    protected boolean doSelfSwitch(Integer to) {
        return false;
    }
}
