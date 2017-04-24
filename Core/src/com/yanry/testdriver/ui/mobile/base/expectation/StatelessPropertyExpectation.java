package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Presentable;

import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 4/24/2017.
 */
public abstract class StatelessPropertyExpectation<V> extends StatelessExpectation {
    private StatelessProperty<V> property;
    private Supplier<V> value;

    public StatelessPropertyExpectation(Timing timing, StatelessProperty<V> property, Supplier<V> value) {
        super(timing);
        this.property = property;
        this.value = value;
    }

    @Presentable
    public StatelessProperty<V> getProperty() {
        return property;
    }

    @Presentable
    public V getValue() {
        return value.get();
    }
}
