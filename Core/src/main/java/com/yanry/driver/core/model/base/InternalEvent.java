package com.yanry.driver.core.model.base;

import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.Objects;

public abstract class InternalEvent<V> extends Event {
    private Property<V> property;

    public InternalEvent(Property<V> property) {
        this.property = property;
    }

    @Visible
    @EqualsPart
    public Property<V> getProperty() {
        return property;
    }

    boolean matches(Property property, Object fromValue, Object toValue) {
        return Objects.equals(this.property, property) && matches((V) fromValue, (V) toValue);
    }

    protected abstract boolean matches(V fromValue, V toValue);

    protected abstract ExternalEvent traverse();
}
