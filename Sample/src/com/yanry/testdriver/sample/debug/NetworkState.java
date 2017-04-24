package com.yanry.testdriver.sample.debug;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.*;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.property.GeneralProperty;

/**
 * Created by rongyu.yan on 2/27/2017.
 */
public class NetworkState extends GeneralProperty<Network> {

    public NetworkState(Graph graph) {
        super(graph, false, Network.values());
        Util.createPath(graph, null, new SwitchNetwork(Network.Normal),
                getExpectation(Timing.IMMEDIATELY, Network.Normal));
        Util.createPath(graph, null, new SwitchNetwork(Network.Abnormal),
                getExpectation(Timing.IMMEDIATELY, Network.Abnormal));
        Util.createPath(graph, null, new SwitchNetwork(Network.Disconnected),
                getExpectation(Timing.IMMEDIATELY, Network.Disconnected));
    }
}
