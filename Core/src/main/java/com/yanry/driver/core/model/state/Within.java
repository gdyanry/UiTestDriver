package com.yanry.driver.core.model.state;

import lib.common.model.EqualsProxy;

import java.util.Collection;
import java.util.stream.Stream;

public class Within<V> implements ValuePredicate<V> {
    private Collection<V> values;
    private EqualsProxy<Within<V>> equalsProxy;

    public Within(Collection<V> values) {
        this.values = values;
        equalsProxy = new EqualsProxy<>(this, e -> e.values);
    }

    @Override
    public boolean test(V value) {
        return values.contains(value);
    }

    @Override
    public Stream<V> getValidValue() {
        return values.stream();
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
