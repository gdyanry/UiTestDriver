package com.yanry.driver.core.model.state;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.Presentable;

@Presentable
public class State<V> {
    private Property<V> property;
    private ValuePredicate<V> valuePredicate;

    public State(Property<V> property, ValuePredicate<V> valuePredicate) {
        this.property = property;
        this.valuePredicate = valuePredicate;
    }

    @Presentable
    public Property<V> getProperty() {
        return property;
    }

    @Presentable
    public ValuePredicate<V> getValuePredicate() {
        return valuePredicate;
    }
}
