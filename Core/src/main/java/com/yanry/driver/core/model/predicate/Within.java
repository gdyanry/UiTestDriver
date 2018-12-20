package com.yanry.driver.core.model.predicate;

import com.yanry.driver.core.model.base.ValuePredicate;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public class Within<V> extends ValuePredicate<V> {
    private Collection<V> values;

    public Within(Collection<V> values) {
        this.values = values;
    }

    public Within(V... values) {
        this.values = Arrays.asList(values);
    }

    @Visible
    @EqualsPart
    public Collection<V> getValues() {
        return values;
    }

    @Override
    public Stream<V> getConcreteValues() {
        return values.stream();
    }

    @Override
    public boolean test(V value) {
        return values.contains(value);
    }
}
