package com.yanry.driver.mobile.sample.login;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.event.SwitchStateAction;
import com.yanry.driver.core.model.runtime.fetch.Select;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by rongyu.yan on 2/27/2017.
 */
public class NetworkState extends Property<NetworkState.Network> {

    public NetworkState(Graph graph) {
        super(graph);
    }

    @Override
    protected Network checkValue() {
        return getGraph().obtainValue(new Select<>(this));
    }

    @Override
    protected ExternalEvent doSelfSwitch(Network to) {
        return new SwitchStateAction<>(this, to);
    }

    @Override
    protected Stream<Network> getValueStream(Set<Network> collectedValues) {
        return Stream.of(Network.values());
    }

    /**
     * Created by rongyu.yan on 3/2/2017.
     */
    public enum Network {
        Normal, Abnormal, Disconnected
    }
}
