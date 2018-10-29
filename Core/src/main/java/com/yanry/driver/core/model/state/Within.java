package com.yanry.driver.core.model.state;

import com.yanry.driver.core.model.base.ValuePredicate;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.Collection;
import java.util.stream.Stream;

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

    @Override
    protected Stream<V> getValidValue() {
        return values.stream();
    }
}
