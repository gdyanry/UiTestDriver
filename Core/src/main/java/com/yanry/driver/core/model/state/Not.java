package com.yanry.driver.core.model.state;

import com.yanry.driver.core.model.base.ValuePredicate;

import java.util.stream.Stream;

public class Not<V> extends ValuePredicate<V> {
    private ValuePredicate<V> predicate;

    public Not(ValuePredicate<V> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(V value) {
        return !predicate.test(value);
    }

    @Override
    protected Stream<V> getValidValue() {
        return null;
    }
}
