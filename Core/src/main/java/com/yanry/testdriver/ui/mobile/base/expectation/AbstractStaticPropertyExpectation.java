package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.Property;

/**
 * A key-value pair (aka state) expectation
 * Created by rongyu.yan on 5/10/2017.
 */
public abstract class AbstractStaticPropertyExpectation<V> extends PropertyExpectation<V> {
    private Property<V> property;

    public AbstractStaticPropertyExpectation(Timing timing, boolean needCheck, Property<V> property) {
        super(timing, needCheck);
        this.property = property;
    }

    @Presentable
    @Override
    public Property<V> getProperty() {
        return property;
    }
}
