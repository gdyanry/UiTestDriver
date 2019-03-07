package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.ActionGuard;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.core.model.property.BooleanProperty;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
public class ProcessState extends BooleanProperty {

    public ProcessState(StateSpace stateSpace) {
        super(stateSpace);
    }

    @Override
    protected Boolean checkValue(Boolean expected) {
        return null;
    }

    @Override
    protected ExternalEvent doSelfSwitch(Boolean to, ActionGuard actionGuard) {
        return null;
    }
}
