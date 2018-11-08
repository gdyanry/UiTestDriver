package com.yanry.driver.core.model.predicate;

import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

public class Between<V extends Comparable<V>> extends ValuePredicate<V> {
    private V lowerBound;
    private V upperBound;

    public Between(V lowerBound, V upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
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

    @Override
    public boolean test(V value) {
        return false;
    }
}
