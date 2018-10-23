package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.InternalEvent;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.ValuePredicate;
import com.yanry.driver.core.model.state.Equals;
import lib.common.util.object.HashAndEquals;
import lib.common.util.object.Presentable;

/**
 * Created by rongyu.yan on 5/17/2017.
 */
public class TransitionEvent<V> extends InternalEvent<V> {
    private ValuePredicate<V> from;
    private ValuePredicate<V> to;

    public TransitionEvent(Property<V> property, ValuePredicate<V> from, ValuePredicate<V> to) {
        super(property);
        this.from = from;
        this.to = to;
    }

    public TransitionEvent(Property<V> property, V from, V to) {
        this(property, from == null ? null : new Equals<>(from), new Equals<>(to));
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
    protected boolean matches(V fromValue, V toValue) {
        return to.test(toValue) && (from == null || from.test(fromValue));
    }
}
