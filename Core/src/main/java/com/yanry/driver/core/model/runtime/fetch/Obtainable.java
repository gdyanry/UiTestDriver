package com.yanry.driver.core.model.runtime.fetch;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.Presentable;

@Presentable
public abstract class Obtainable<V> {
    private Property<V> property;

    public Obtainable(Property<V> property) {
        this.property = property;
    }

    @Presentable
    public Property<V> getProperty() {
        return property;
    }

    public abstract V convert(String fetchedValue);
}
