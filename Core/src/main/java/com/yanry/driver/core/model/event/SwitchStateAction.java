package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.expectation.Timing;
import lib.common.util.object.HashAndEquals;
import lib.common.util.object.Presentable;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class SwitchStateAction<V> extends ExpectationEvent {
    private Property<V> property;
    private V to;

    public SwitchStateAction(Property<V> property, V to) {
        super(property.getStaticExpectation(Timing.IMMEDIATELY, false, to));
        this.property = property;
        this.to = to;
    }

    @HashAndEquals
    @Presentable
    public Property<V> getProperty() {
        return property;
    }

    @HashAndEquals
    @Presentable
    public V getTo() {
        return to;
    }
}
