package com.yanry.testdriver.sample.reservation.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.property.UnsearchableSwitchableProperty;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.extend.action.SwitchState;

/**
 * Created by rongyu.yan on 5/18/2017.
 */
public class NetworkConnectivity extends UnsearchableSwitchableProperty<Boolean> {
    private Graph graph;

    public NetworkConnectivity(Graph graph) {
        this.graph = graph;
    }

    @Override
    protected Boolean checkValue() {
        return graph.checkState(new StateToCheck<Boolean>(this, false, true));
    }

    @Override
    protected boolean doSwitch(Boolean to) {
        return graph.performAction(new SwitchState(this, to));
    }

    @Override
    protected Graph getGraph() {
        return graph;
    }
}
