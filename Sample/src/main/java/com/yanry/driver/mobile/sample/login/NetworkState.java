package com.yanry.driver.mobile.sample.login;

import com.yanry.driver.core.model.base.ActionGuard;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.core.model.event.SwitchStateAction;
import com.yanry.driver.core.model.runtime.fetch.Select;
import yanry.lib.java.util.ReflectionUtil;

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

    public NetworkState(StateSpace stateSpace) {
        super(stateSpace);
    }

    @Override
    protected String checkValue(String expected) {
        return getStateSpace().obtainValue(new Select<>(this), expected);
    }

    @Override
    protected ExternalEvent doSelfSwitch(String to, ActionGuard actionGuard) {
        return new SwitchStateAction<>(this, to);
    }

    @Override
    protected Stream<String> getValueStream(Set<String> collectedValues) {
        return ReflectionUtil.getStaticStringFieldNames(getClass()).stream();
    }
}
