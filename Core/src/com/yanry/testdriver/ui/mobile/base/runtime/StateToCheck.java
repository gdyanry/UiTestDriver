package com.yanry.testdriver.ui.mobile.base.runtime;

import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;

/**
 * Created by rongyu.yan on 3/13/2017.
 */
@Presentable
public class StateToCheck<V> {
    private CacheProperty<V> property;
    private V[] options;

    public StateToCheck(CacheProperty<V> property, V...options) {
        this.property = property;
        this.options = options;
    }

    @Presentable
    public CacheProperty<V> getProperty() {
        return property;
    }

    @Presentable
    public V[] getOptions() {
        return options;
    }
}
