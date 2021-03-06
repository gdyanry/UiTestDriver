package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.predicate.Equals;
import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.Visible;

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
        property.findValueToAdd(from);
        property.findValueToAdd(to);
    }

    public TransitionEvent(Property<V> property, V from, V to) {
        this(property, Equals.of(from), Equals.of(to));
    }

    @EqualsPart
    @Visible
    public ValuePredicate<V> getFrom() {
        return from;
    }

    @EqualsPart
    @Visible
    public ValuePredicate<V> getTo() {
        return to;
    }

    @Override
    protected boolean matches(V fromValue, V toValue) {
        return to.test(toValue) && from.test(fromValue);
    }

    @Override
    protected ExternalEvent traverse(ActionGuard actionGuard) {
        Property<V> property = getProperty();
        if (!from.test(property.getCurrentValue())) {
            return property.switchTo(from, actionGuard);
        } else {
            return property.switchTo(to, actionGuard);
        }
    }
}
