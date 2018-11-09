package com.yanry.driver.core.model.base;

import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.stream.Stream;

public class Not<V> extends ValuePredicate<V> {
    private ValuePredicate<V> predicate;

    Not(ValuePredicate<V> predicate) {
        this.predicate = predicate;
    }

    @Visible
    @EqualsPart
    public ValuePredicate<V> getPredicate() {
        return predicate;
    }

    @Override
    public ValuePredicate<V> not() {
        return predicate;
    }

    @Override
    public Stream<V> getConcreteValues() {
        return predicate.getConcreteValues();
    }

    @Override
    public boolean test(V value) {
        return !predicate.test(value);
    }
}
