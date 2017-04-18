package com.yanry.testdriver.ui.mobile.model.base;

/**
 * Created by rongyu.yan on 3/13/2017.
 */
@Presentable
public class StateToCheck<V> {
    private ObjectProperty<V> property;
    private V[] options;
    private Timing timing;

    public StateToCheck(ObjectProperty<V> property, V[] options, Timing timing) {
        this.property = property;
        this.options = options;
        this.timing = timing;
    }

    @Presentable
    public ObjectProperty<V> getProperty() {
        return property;
    }

    @Presentable
    public V[] getOptions() {
        return options;
    }

    @Presentable
    public Timing getTiming() {
        return timing;
    }
}
