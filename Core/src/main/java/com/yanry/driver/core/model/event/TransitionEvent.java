package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Event;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.ValuePredicate;
import com.yanry.driver.core.model.state.Equals;
import lib.common.util.object.HashAndEquals;
import lib.common.util.object.Presentable;

/**
 * Created by rongyu.yan on 5/17/2017.
 */
@Presentable
public class TransitionEvent<V> extends Event {
    private Property<V> property;
    private ValuePredicate<V> from;
    private ValuePredicate<V> to;

    public TransitionEvent(Property<V> property, ValuePredicate<V> from, ValuePredicate<V> to) {
        this.property = property;
        this.from = from;
        this.to = to;
    }

    public TransitionEvent(Property<V> property, V from, V to) {
        this(property, from == null ? null : new Equals<>(from), new Equals<>(to));
    }

    @HashAndEquals
    @Presentable
    public Property<V> getProperty() {
        return property;
    }

    @HashAndEquals
    @Presentable
    public ValuePredicate<V> getFrom() {
        return from;
    }

    @HashAndEquals
    @Presentable
    public ValuePredicate<V> getTo() {
        return to;
    }

    @Override
    protected boolean matches(Property property, Object fromValue, Object toValue) {
        return this.property.equals(property) && this.to.test((V) toValue) && (from == null || this.from.test((V) fromValue));
    }
}
