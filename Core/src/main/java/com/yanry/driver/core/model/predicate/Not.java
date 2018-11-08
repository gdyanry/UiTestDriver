package com.yanry.driver.core.model.predicate;

import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

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
    public boolean test(V value) {
        return !predicate.test(value);
    }
}
