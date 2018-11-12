package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Property;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class SwitchStateAction<V> extends ExternalEvent {
    private Property<V> property;
    private V value;

    public SwitchStateAction(Property<V> property, V value) {
        this.property = property;
        this.value = value;
    }

    @Visible
    @EqualsPart
    public Property<V> getProperty() {
        return property;
    }

    @Visible
    @EqualsPart
    public V getValue() {
        return value;
    }
}
