package com.yanry.testdriver.ui.mobile.model.base;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
@Presentable
public class PermanentExpectation<V> implements Expectation {
    private ObjectProperty<V> property;
    private V value;
    private Timing timing;

    public PermanentExpectation(ObjectProperty<V> property, V value, Timing timing) {
        this.property = property;
        this.value = value;
        this.timing = timing;
    }

    @Presentable
    public ObjectProperty<V> getProperty() {
        return property;
    }

    @Presentable
    public V getValue() {
        return value;
    }

    @Presentable
    public Timing getTiming() {
        return timing;
    }
}
