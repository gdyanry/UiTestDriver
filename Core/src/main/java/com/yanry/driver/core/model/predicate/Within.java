package com.yanry.driver.core.model.predicate;

import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.Collection;

public class Within<V> extends ValuePredicate<V> {
    private Collection<V> values;

    public Within(Collection<V> values) {
        this.values = values;
    }

    @Visible
    @EqualsPart
    public Collection<V> getValues() {
        return values;
    }

    @Override
    public boolean test(V value) {
        return values.contains(value);
    }
}
