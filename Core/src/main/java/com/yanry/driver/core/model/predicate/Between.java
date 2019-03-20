package com.yanry.driver.core.model.predicate;

import com.yanry.driver.core.model.base.ValuePredicate;
import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.Visible;

import java.util.stream.Stream;

public class Between<V extends Comparable<V>> extends ValuePredicate<V> {
    private V lowerBound;
    private boolean includeLowerBound;
    private V upperBound;
    private boolean includeUpperBound;

    public Between(V lowerBound, boolean includeLowerBound, V upperBound, boolean includeUpperBound) {
        this.lowerBound = lowerBound;
        this.includeLowerBound = includeLowerBound;
        this.upperBound = upperBound;
        this.includeUpperBound = includeUpperBound;
    }

    @Visible
    @EqualsPart
    public V getLowerBound() {
        return lowerBound;
    }

    @Visible
    @EqualsPart
    public V getUpperBound() {
        return upperBound;
    }

    @Visible
    @EqualsPart
    public boolean isIncludeLowerBound() {
        return includeLowerBound;
    }

    @Visible
    @EqualsPart
    public boolean isIncludeUpperBound() {
        return includeUpperBound;
    }

    @Override
    public Stream<V> getConcreteValues() {
        return Stream.of(lowerBound, upperBound);
    }

    @Override
    public boolean test(V value) {
        return (includeLowerBound ? value.compareTo(lowerBound) >= 0 : value.compareTo(lowerBound) > 0)
                && (includeUpperBound ? value.compareTo(upperBound) <= 0 : value.compareTo(upperBound) < 0);
    }
}
