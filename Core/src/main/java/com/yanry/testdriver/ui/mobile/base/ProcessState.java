package com.yanry.testdriver.ui.mobile.base;

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
    protected boolean doSelfSwitch(Graph graph, Boolean to) {
        return false;
    }
}
