package com.yanry.driver.core.model.expectation;

import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.core.model.base.Property;

public class SSPropertyExpectation<V> extends StaticPropertyExpectation<V> {
    private V value;

    public SSPropertyExpectation(Timing timing, boolean needCheck, Property<V> property, V value) {
        super(timing, needCheck, property);
        this.value = value;
    }

    @Presentable
    @Override
    public V getExpectedValue() {
        return value;
    }
}
