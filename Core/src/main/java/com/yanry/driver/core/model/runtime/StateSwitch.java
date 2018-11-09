package com.yanry.driver.core.model.runtime;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.ValuePredicate;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.HandyObject;
import lib.common.util.object.Visible;

public class StateSwitch<V> extends HandyObject {
    private Property<V> property;
    private V from;
    private ValuePredicate<V> to;

    public StateSwitch(Property<V> property, V from, ValuePredicate<V> to) {
        this.property = property;
        this.from = from;
        this.to = to;
    }

    @Visible
    @EqualsPart
    public Property<V> getProperty() {
        return property;
    }

    @Visible
    @EqualsPart
    public V getFrom() {
        return from;
    }

    @Visible
    @EqualsPart
    public ValuePredicate<V> getTo() {
        return to;
    }
}
