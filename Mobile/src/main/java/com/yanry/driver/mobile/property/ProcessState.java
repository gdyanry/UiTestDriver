package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.event.SwitchStateAction;
import com.yanry.driver.core.model.property.CacheProperty;
import com.yanry.driver.core.model.property.Property;

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
