package com.yanry.driver.mobile.sample.login;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.event.SwitchStateAction;
import com.yanry.driver.core.model.base.CacheProperty;
import com.yanry.driver.core.model.runtime.StateToCheck;

/**
 * Created by rongyu.yan on 2/27/2017.
 */
public class NetworkState extends CacheProperty<NetworkState.Network> {

    public NetworkState(Graph graph) {
        super(graph);
    }

    @Override
    protected Network checkValue() {
        return getGraph().checkState(new StateToCheck<>(this, Network.values()));
    }

    @Override
    protected boolean doSelfSwitch(Network to) {
        return getGraph().performAction(new SwitchStateAction<>(this, to));
    }

    /**
     * Created by rongyu.yan on 3/2/2017.
     */
    public enum Network {
        Normal, Abnormal, Disconnected
    }
}
