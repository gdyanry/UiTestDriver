package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.predicate.And;
import com.yanry.driver.core.model.predicate.CompoundPredicate;
import com.yanry.driver.core.model.predicate.Or;
import lib.common.util.object.HandyObject;

import java.util.stream.Stream;

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

    public abstract Stream<V> getConcreteValues();

    public abstract boolean test(V value);
}
