package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.core.model.state.Equals;
import com.yanry.driver.core.model.state.ValuePredicate;
import lib.common.model.EqualsProxy;

/**
 * Created by rongyu.yan on 5/17/2017.
 */
@Presentable
public class TransitionEvent<V> implements Event {
    private Property<V> property;
    private ValuePredicate<V> from;
    private ValuePredicate<V> to;
    private EqualsProxy<TransitionEvent<V>> equalsProxy;

    public TransitionEvent(Property<V> property, ValuePredicate<V> from, ValuePredicate<V> to) {
        this.property = property;
        this.from = from;
        this.to = to;
        equalsProxy = new EqualsProxy<>(this, s -> s.property, s -> s.from, s -> s.to);
    }

    public TransitionEvent(Property<V> property, V from, V to) {
        this(property, from == null ? null : new Equals<>(from), new Equals<>(to));
    }

    @Presentable
    public Property<V> getProperty() {
        return property;
    }

    @Presentable
    public ValuePredicate<V> getFrom() {
        return from;
    }

    @Presentable
    public ValuePredicate<V> getTo() {
        return to;
    }

    @Override
    public boolean matches(Property property, Object fromValue, Object toValue) {
        return this.property.equals(property) && this.to.test((V) toValue) && (from == null || this.from.test((V) fromValue));
    }

    @Override
    public int hashCode() {
        return equalsProxy.getHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return equalsProxy.checkEquals(obj);
    }
}
