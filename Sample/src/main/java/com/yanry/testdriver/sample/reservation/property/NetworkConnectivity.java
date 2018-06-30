package com.yanry.testdriver.sample.reservation.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.base.event.SwitchStateAction;

/**
 * Created by rongyu.yan on 5/18/2017.
 */
public class NetworkConnectivity extends CacheProperty<Boolean> {

    @Override
    protected Boolean checkValue(Graph graph) {
        return graph.checkState(new StateToCheck<>(this, false, true));
    }

    @Override
    protected boolean doSelfSwitch(Graph graph, Boolean to) {
        return graph.performAction(new SwitchStateAction(this, to));
    }
}
