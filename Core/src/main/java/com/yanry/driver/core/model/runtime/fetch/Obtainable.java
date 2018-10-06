package com.yanry.driver.core.model.runtime.fetch;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
import lib.common.util.object.Presentable;

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

    public String serialize() {
        return Graph.getPresentation(this).toString();
    }

    public abstract V convert(String responseString);
}
