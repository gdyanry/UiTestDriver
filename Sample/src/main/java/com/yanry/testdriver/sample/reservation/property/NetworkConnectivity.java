package com.yanry.testdriver.sample.reservation.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.extend.action.SwitchState;

/**
 * Created by rongyu.yan on 5/18/2017.
 */
public class NetworkConnectivity extends CacheProperty<Boolean> {

    @Override
    protected Boolean checkValue(Graph graph) {
        return graph.checkState(new StateToCheck<>(this, false, true));
    }

    @Override
    public boolean isCheckedByUser() {
        return false;
    }

    @Override
    protected boolean selfSwitch(Graph graph, Boolean to) {
        return graph.performAction(new SwitchState(this, to));
    }
}
