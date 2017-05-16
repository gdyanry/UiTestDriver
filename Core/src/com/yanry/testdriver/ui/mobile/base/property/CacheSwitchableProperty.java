package com.yanry.testdriver.ui.mobile.base.property;

/**
 * state property that is supposed to be used as expectation of a path.
 * <p>
 * Created by rongyu.yan on 5/9/2017.
 */
public abstract class CacheSwitchableProperty<V> extends SwitchableProperty<V> {
    private V cacheValue;

    protected abstract V checkValue();

    void setCacheValue(V cacheValue) {
        this.cacheValue = cacheValue;
    }

    @Override
    public V getCurrentValue() {
        if (cacheValue == null) {
            cacheValue = checkValue();
        }
        return cacheValue;
    }
}
