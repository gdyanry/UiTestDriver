package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.property.Property;

import java.util.function.BiPredicate;

public class StaticPropertyExpectation<V> extends PropertyExpectation<V> {
    private V value;

    public StaticPropertyExpectation(Timing timing, boolean needCheck, Property<V> property, V value) {
        super(timing, needCheck, property);
        this.value = value;
    }

    public boolean isSatisfied(BiPredicate<Property<V>, V> predicate) {
        return predicate.test(getProperty(), value);
    }

    @Presentable
    @Override
    public V getExpectedValue() {
        return value;
    }
}
