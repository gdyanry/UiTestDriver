package com.yanry.testdriver.sample.reservation.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.property.UnsearchableProperty;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.extend.action.SwitchState;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 5/18/2017.
 */
public class NetworkConnectivity extends UnsearchableProperty<Boolean> {
    private Graph graph;

    public NetworkConnectivity(Graph graph) {
        this.graph = graph;
    }

    @Override
    protected Boolean checkValue() {
        return graph.checkState(new StateToCheck<Boolean>(this, false, true));
    }

    @Override
    protected boolean doSwitch(Boolean to, List<Path> superPathContainer, Supplier<Boolean> finalCheck) {
        return graph.performAction(new SwitchState(this, to));
    }

    @Override
    protected Graph getGraph() {
        return graph;
    }
}
