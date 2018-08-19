package com.yanry.driver.core.model.state;

import java.util.Collection;
import java.util.stream.Stream;

public class ValueWithin<V> implements ValuePredicate<V> {
    private Collection<V> values;

    public ValueWithin(Collection<V> values) {
        this.values = values;
    }

    @Override
    public boolean test(V value) {
        return values.contains(value);
    }

    @Override
    public Stream<V> getValidValue() {
        return values.stream();
    }
}
