package com.yanry.testdriver.ui.mobile.extend.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.event.SwitchStateAction;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.property.Property;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
public class ProcessState extends CacheProperty<Boolean> {

    public ProcessState(Graph graph) {
        super(graph);
    }

    @Override
    protected Boolean checkValue() {
        return false;
    }

    @Override
    protected boolean doSelfSwitch(Boolean to) {
        return getGraph().performAction(new SwitchStateAction(this, to));
    }

    @Override
    protected boolean equalsWithSameClass(Property<Boolean> property) {
        return true;
    }
}
