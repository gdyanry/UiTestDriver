package com.yanry.testdriver.ui.mobile.base.runtime;

import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.StateProperty;

/**
 * Created by rongyu.yan on 3/13/2017.
 */
@Presentable
public class StateToCheck<V> {
    private StateProperty<V> property;
    private V[] options;

    public StateToCheck(StateProperty<V> property, V[] options) {
        this.property = property;
        this.options = options;
    }

    @Presentable
    public StateProperty<V> getProperty() {
        return property;
    }

    @Presentable
    public V[] getOptions() {
        return options;
    }
}
