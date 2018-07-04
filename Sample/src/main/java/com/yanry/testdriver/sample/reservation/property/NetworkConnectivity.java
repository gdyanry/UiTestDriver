package com.yanry.testdriver.sample.reservation.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.property.Property;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.base.event.SwitchStateAction;

/**
 * Created by rongyu.yan on 5/18/2017.
 */
public class NetworkConnectivity extends CacheProperty<Boolean> {

    public NetworkConnectivity(Graph graph) {
        super(graph);
    }

    @Override
    protected Boolean checkValue() {
        return getGraph().checkState(new StateToCheck<>(this, false, true));
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
