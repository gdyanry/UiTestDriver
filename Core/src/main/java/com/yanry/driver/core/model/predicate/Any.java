package com.yanry.driver.core.model.predicate;

import com.yanry.driver.core.model.base.ValuePredicate;

import java.util.stream.Stream;

public class Any<V> extends ValuePredicate<V> {
    @Override
    public Stream<V> getConcreteValues() {
        return null;
    }

    @Override
    public boolean test(V value) {
        return true;
    }
}
