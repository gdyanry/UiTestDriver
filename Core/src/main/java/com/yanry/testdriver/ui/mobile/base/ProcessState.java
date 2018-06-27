package com.yanry.testdriver.ui.mobile.base;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.event.SwitchStateAction;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
public class ProcessState extends CacheProperty<Boolean> {

    public ProcessState() {
    }

    @Override
    protected Boolean checkValue(Graph graph) {
        return false;
    }

    @Override
    public boolean isCheckedByUser() {
        return false;
    }

    @Override
    protected boolean doSelfSwitch(Graph graph, Boolean to) {
        return graph.performAction(new SwitchStateAction(this, to));
    }
}
