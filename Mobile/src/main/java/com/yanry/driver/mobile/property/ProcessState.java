package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.Graph;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
public class ProcessState extends Property<Boolean> {

    public ProcessState(Graph graph) {
        super(graph);
    }

    @Override
    protected Boolean checkValue() {
        return false;
    }

    @Override
    protected SwitchResult doSelfSwitch(Boolean to) {
        return SwitchResult.NoAction;
    }
}
