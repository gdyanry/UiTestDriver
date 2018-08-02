package com.yanry.driver.mobile.sample.reservation.property;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.event.SwitchStateAction;
import com.yanry.driver.core.model.property.CacheProperty;
import com.yanry.driver.core.model.property.Property;
import com.yanry.driver.core.model.runtime.StateToCheck;

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
}
