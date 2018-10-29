package com.yanry.driver.core.model.runtime.fetch;

import com.yanry.driver.core.model.base.Property;
import lib.common.util.object.Visible;
import lib.common.util.object.VisibleObject;

public abstract class Obtainable<V> extends VisibleObject {
    private Property<V> property;

    public Obtainable(Property<V> property) {
        this.property = property;
    }

    @Visible
    public Property<V> getProperty() {
        return property;
    }

    public abstract V convert(String responseString);
}
