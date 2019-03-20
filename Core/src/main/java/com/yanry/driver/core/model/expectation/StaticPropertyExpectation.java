package com.yanry.driver.core.model.expectation;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.PropertyExpectation;
import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.Visible;

/**
 * A key-value pair (aka state) expectation
 * Created by rongyu.yan on 5/10/2017.
 */
public abstract class StaticPropertyExpectation<V> extends PropertyExpectation<V> {
    private Property<V> property;

    public StaticPropertyExpectation(Timing timing, boolean needCheck, Property<V> property) {
        super(timing, needCheck);
        this.property = property;
    }

    @Visible
    @EqualsPart
    @Override
    public Property<V> getProperty() {
        return property;
    }
}
