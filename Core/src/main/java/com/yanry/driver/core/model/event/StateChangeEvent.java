package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Event;
import com.yanry.driver.core.model.base.Property;
import lib.common.util.object.Presentable;

import java.util.Objects;

public class StateChangeEvent extends Event {
    private Property property;

    public StateChangeEvent(Property property) {
        this.property = property;
    }

    @Presentable
    public Property getProperty() {
        return property;
    }

    @Override
    protected boolean matches(Property property, Object fromValue, Object toValue) {
        return this.property.equals(property) && !Objects.equals(fromValue, toValue);
    }
}
