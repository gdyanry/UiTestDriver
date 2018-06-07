package com.yanry.testdriver.sample.debug;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.property.SwitchBySelfProperty;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.extend.action.SwitchState;

/**
 * Created by rongyu.yan on 2/27/2017.
 */
public class NetworkState extends SwitchBySelfProperty<NetworkState.Network> {
    private Graph graph;

    public NetworkState(Graph graph) {
        this.graph = graph;
    }

    @Override
    protected Network checkValue() {
        return graph.checkState(new StateToCheck<>(this, Network.values()));
    }

    @Override
    protected boolean dooSwitch(Network to) {
        return getGraph().performAction(new SwitchState<>(this, to));
    }

    @Override
    protected Graph getGraph() {
        return graph;
    }

    /**
     * Created by rongyu.yan on 3/2/2017.
     */
    public enum Network {
        Normal, Abnormal, Disconnected
    }
}
