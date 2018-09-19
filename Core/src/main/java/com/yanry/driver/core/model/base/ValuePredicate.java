package com.yanry.driver.core.model.base;

import lib.common.util.object.ObjectUtil;

import java.util.stream.Stream;

public abstract class ValuePredicate<V> {
    @Override
    public final int hashCode() {
        return ObjectUtil.hashCode(this);
    }

    @Override
    public final boolean equals(Object obj) {
        return ObjectUtil.equals(this, obj);
    }

    public abstract boolean test(V value);

    protected abstract Stream<V> getValidValue();
}
