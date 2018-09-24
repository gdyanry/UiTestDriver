package com.yanry.driver.mobile.sample.login;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.event.SwitchStateAction;
import com.yanry.driver.core.model.runtime.fetch.Select;

/**
 * Created by rongyu.yan on 2/27/2017.
 */
public class NetworkState extends Property<NetworkState.Network> {

    public NetworkState(Graph graph) {
        super(graph);
    }

    @Override
    protected Network fetchValue() {
        return getGraph().obtainValue(new Select<>(this, Network.values()));
    }

    @Override
    protected ActionEvent doSelfSwitch(Network to) {
        return new SwitchStateAction<>(this, to);
    }

    /**
     * Created by rongyu.yan on 3/2/2017.
     */
    public enum Network {
        Normal, Abnormal, Disconnected
    }
}
