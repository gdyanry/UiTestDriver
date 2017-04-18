package com.yanry.testdriver.ui.mobile.model.test;

import com.yanry.testdriver.ui.mobile.model.base.*;

/**
 * Created by rongyu.yan on 2/27/2017.
 */
public class NetworkState extends GeneralProperty<Network> {

    public NetworkState(Graph graph) {
        super(false, graph, Network.values());
        new Path(graph, null, new SwitchNetwork(Network.Normal),
                new PermanentExpectation<>(this, Network.Normal, Timing.IMMEDIATELY));
        new Path(graph, null, new SwitchNetwork(Network.Abnormal),
                new PermanentExpectation<>(this, Network.Abnormal, Timing.IMMEDIATELY));
        new Path(graph, null, new SwitchNetwork(Network.Disconnected),
                new PermanentExpectation<>(this, Network.Disconnected, Timing.IMMEDIATELY));
    }
}
