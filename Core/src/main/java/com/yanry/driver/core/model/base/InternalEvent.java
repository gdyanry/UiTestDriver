package com.yanry.driver.core.model.base;

import lib.common.util.object.HashAndEquals;
import lib.common.util.object.Presentable;

import java.util.Objects;

public abstract class InternalEvent<V> extends Event {
    private Property<V> property;

    public InternalEvent(Property<V> property) {
        this.property = property;
    }

    @Presentable
    @HashAndEquals
    public Property getProperty() {
        return property;
    }

    boolean matches(Property property, Object fromValue, Object toValue) {
        return Objects.equals(this.property, property) && matches((V) fromValue, (V) toValue);
    }

    protected abstract boolean matches(V fromValue, V toValue);
}
