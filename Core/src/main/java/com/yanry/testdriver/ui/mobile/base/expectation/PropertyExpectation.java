package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.Property;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

/** A key-value pair (aka state) expectation
 * Created by rongyu.yan on 5/10/2017.
 */
public abstract class PropertyExpectation<V> extends Expectation {
    private Property<V> property;

    public PropertyExpectation(Timing timing, Property<V> property) {
        super(timing);
        this.property = property;
    }

    @Presentable
    public Property<V> getProperty() {
        return property;
    }
}
