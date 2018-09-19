package com.yanry.driver.core.model.runtime;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.ValuePredicate;
import lib.common.util.object.Presentable;

@Presentable
public class StateSwitch<V> {
    private Property<V> property;
    private V from;
    private ValuePredicate<V> to;

    public StateSwitch(Property<V> property, V from, ValuePredicate<V> to) {
        this.property = property;
        this.from = from;
        this.to = to;
    }

    @Presentable
    public Property<V> getProperty() {
        return property;
    }

    @Presentable
    public V getFrom() {
        return from;
    }

    @Presentable
    public ValuePredicate<V> getTo() {
        return to;
    }
}
