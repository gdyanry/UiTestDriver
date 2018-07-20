package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.property.Property;
import com.yanry.driver.core.model.runtime.Presentable;

import java.util.function.Predicate;

@Presentable
public class StateChangeCallback<V> implements Event<V> {
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
    public boolean matches(Property<V> property, V fromValue, V toValue) {
        return this.property.equals(property) && to.test(toValue) && (from == null || from.test(fromValue));
    }
}
