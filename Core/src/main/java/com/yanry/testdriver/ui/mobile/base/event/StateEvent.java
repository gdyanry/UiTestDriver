package com.yanry.testdriver.ui.mobile.base.event;

import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.Property;

/**
 * Created by rongyu.yan on 5/17/2017.
 */
@Presentable
public class StateEvent<V> implements Event<V> {
    private Property<V> property;
    private V from;
    private V to;

    public StateEvent(Property<V> property, V from, V to) {
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
    public V getTo() {
        return to;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !obj.getClass().equals(getClass())) {
            return false;
        }
        StateEvent event = (StateEvent) obj;
        return property.equals(event.getProperty()) && from.equals(event.getFrom()) && to.equals(event.getTo());
    }

    @Override
    public boolean matches(Property<V> property, V fromValue, V toValue) {
        return this.property.equals(property) && this.to.equals(toValue) && (from == null || this.from.equals(fromValue));
    }
}
