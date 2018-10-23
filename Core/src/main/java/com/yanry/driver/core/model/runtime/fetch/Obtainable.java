package com.yanry.driver.core.model.runtime.fetch;

import com.yanry.driver.core.model.base.Property;
import lib.common.util.object.HandyObject;
import lib.common.util.object.Presentable;

public abstract class Obtainable<V> extends HandyObject {
    private Property<V> property;

    public Obtainable(Property<V> property) {
        this.property = property;
    }

    @Presentable
    public Property<V> getProperty() {
        return property;
    }

    public abstract V convert(String responseString);
}
