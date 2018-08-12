package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Event;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.Presentable;

import java.util.ArrayList;
import java.util.function.Predicate;

@Presentable
public class StateChangeCallback<V> extends Event {
    private Property<V> property;
    private Predicate<V> from;
    private Predicate<V> to;

    public StateChangeCallback(Property<V> property, Predicate<V> from, Predicate<V> to) {
        this.property = property;
        this.from = from;
        this.to = to;
    }

    @Presentable
    public Property<V> getProperty() {
        return property;
    }

    public Predicate<V> getFrom() {
        return from;
    }

    public Predicate<V> getTo() {
        return to;
    }

    @Override
    public <T> boolean matches(Property<T> property, T fromValue, T toValue) {
        return this.property.equals(property) && to.test((V) toValue) && (from == null || from.test((V) fromValue));
    }

    @Override
    protected void addHashFields(ArrayList<Object> hashFields) {
        hashFields.add(property);
        hashFields.add(from);
        hashFields.add(to);
    }

    @Override
    protected boolean equalsWithSameClass(Object object) {
        StateChangeCallback changeCallback = (StateChangeCallback) object;
        return property.equals(changeCallback.getProperty());
    }
}
