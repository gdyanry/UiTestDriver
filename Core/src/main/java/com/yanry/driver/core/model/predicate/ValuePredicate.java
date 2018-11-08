package com.yanry.driver.core.model.predicate;

import lib.common.util.object.HandyObject;

public abstract class ValuePredicate<V> extends HandyObject {
    private Not<V> not;

    public ValuePredicate<V> not() {
        if (not == null) {
            not = new Not<>(this);
        }
        return not;
    }

    public CompoundPredicate<V> and(ValuePredicate<V> predicate) {
        return new And<>(this, predicate);
    }

    public CompoundPredicate<V> or(ValuePredicate<V> predicate) {
        return new Or<>(this, predicate);
    }

    public abstract boolean test(V value);
}
