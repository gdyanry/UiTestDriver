package com.yanry.testdriver.sample.reservation.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.property.SwitchBySelfProperty;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.extend.action.SwitchState;

/**
 * Created by rongyu.yan on 5/18/2017.
 */
public class NetworkConnectivity extends SwitchBySelfProperty<Boolean> {
    private Graph graph;

    public NetworkConnectivity(Graph graph) {
        this.graph = graph;
    }

    @Override
    protected Boolean checkValue() {
        return graph.checkState(new StateToCheck<>(this, false, true));
    }

    @Override
    protected boolean dooSwitch(Boolean to) {
        return graph.performAction(new SwitchState(this, to));
    }

    @Override
    protected Graph getGraph() {
        return graph;
    }
}
