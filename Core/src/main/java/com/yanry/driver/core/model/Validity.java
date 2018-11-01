package com.yanry.driver.core.model;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.ValuePredicate;

/**
 * @Author: yanry
 * @Date: 2018/10/31 23:10
 */
public class Validity<V> extends Property<Boolean> {
    private Property<V> property;

    public Validity(Property<V> property, ValuePredicate<V> validPredicate, ValuePredicate<V> invalidPredicate) {
        super(property.getGraph());
        this.property = property;
    }

    public Validity(Graph graph) {
        super(graph);
    }

    @Override
    protected Boolean checkValue() {
        return null;
    }

    @Override
    protected ExternalEvent doSelfSwitch(Boolean to) {
        return null;
    }
}
