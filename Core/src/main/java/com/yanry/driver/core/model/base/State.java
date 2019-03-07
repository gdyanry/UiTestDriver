package com.yanry.driver.core.model.base;

import lib.common.util.object.EqualsPart;
import lib.common.util.object.HandyObject;
import lib.common.util.object.Visible;

public class State<V> extends HandyObject {
    private Property<V> property;
    private ValuePredicate<V> valuePredicate;

    State(Property<V> property, ValuePredicate<V> valuePredicate) {
        this.property = property;
        this.valuePredicate = valuePredicate;
    }

    public boolean isSatisfied() {
        return valuePredicate.test(property.getCurrentValue());
    }

    public ExternalEvent trySatisfy(ActionGuard actionGuard) {
        return property.switchTo(valuePredicate, actionGuard);
    }

    @Visible
    @EqualsPart
    public Property<V> getProperty() {
        return property;
    }

    @Visible
    @EqualsPart
    public ValuePredicate<V> getValuePredicate() {
        return valuePredicate;
    }
}
