package com.yanry.driver.mobile.sample.login;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.event.SwitchStateAction;
import com.yanry.driver.core.model.runtime.fetch.Select;
import lib.common.util.ReflectionUtil;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by rongyu.yan on 2/27/2017.
 */
public class NetworkState extends Property<String> {
    static {
        ReflectionUtil.initStaticStringFields(NetworkState.class);
    }

    public static String Normal;
    public static String Abnormal;
    public static String Disconnected;

    public NetworkState(Graph graph) {
        super(graph);
    }

    @Override
    protected String checkValue() {
        return getGraph().obtainValue(new Select<>(this));
    }

    @Override
    protected ExternalEvent doSelfSwitch(String to) {
        return new SwitchStateAction<>(this, to);
    }

    @Override
    protected Stream<String> getValueStream(Set<String> collectedValues) {
        return ReflectionUtil.getStaticStringFieldNames(getClass()).stream();
    }
}
