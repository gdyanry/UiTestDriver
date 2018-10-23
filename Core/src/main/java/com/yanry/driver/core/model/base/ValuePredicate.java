package com.yanry.driver.core.model.base;

import lib.common.util.object.HandyObject;

import java.util.stream.Stream;

public abstract class ValuePredicate<V> extends HandyObject {
    public abstract boolean test(V value);

    protected abstract Stream<V> getValidValue();
}
