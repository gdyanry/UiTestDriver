package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.BooleanProperty;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Graph;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
public class ProcessState extends BooleanProperty {

    public ProcessState(Graph graph) {
        super(graph);
    }

    @Override
    protected Boolean checkValue() {
        return false;
    }

    @Override
    protected ExternalEvent doSelfSwitch(Boolean to) {
        return null;
    }
}
