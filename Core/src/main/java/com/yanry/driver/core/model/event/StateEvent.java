package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Event;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.Presentable;

import java.util.ArrayList;

/**
 * Created by rongyu.yan on 5/17/2017.
 */
@Presentable
public class StateEvent<V> extends Event {
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
    public <V> boolean matches(Property<V> property, V fromValue, V toValue) {
        return this.property.equals(property) && this.to.equals(toValue) && (from == null || this.from.equals(fromValue));
    }

    @Override
    protected void addHashFields(ArrayList<Object> hashFields) {
        hashFields.add(property);
        hashFields.add(from);
        hashFields.add(to);
    }

    @Override
    protected boolean equalsWithSameClass(Object object) {
        StateEvent event = (StateEvent) object;
        return property.equals(event.getProperty()) && (from == null ? event.from == null : from.equals(event.getFrom())) && to.equals(event.getTo());
    }
}
