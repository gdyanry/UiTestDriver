package com.yanry.testdriver.sample.debug;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.event.SwitchStateAction;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;

/**
 * Created by rongyu.yan on 2/27/2017.
 */
public class NetworkState extends CacheProperty<NetworkState.Network> {

    @Override
    protected Network checkValue(Graph graph) {
        return graph.checkState(new StateToCheck<>(this, Network.values()));
    }

    @Override
    protected boolean doSelfSwitch(Graph graph, Network to) {
        return graph.performAction(new SwitchStateAction<>(this, to));
    }

    /**
     * Created by rongyu.yan on 3/2/2017.
     */
    public enum Network {
        Normal, Abnormal, Disconnected
    }
}
