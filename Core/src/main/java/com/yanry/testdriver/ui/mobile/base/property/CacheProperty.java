package com.yanry.testdriver.ui.mobile.base.property;

/**
 * state property that is supposed to be used as expectation of a path.
 * <p>
 * Created by rongyu.yan on 5/9/2017.
 */
public abstract class CacheProperty<V> extends Property<V> {
    private V cacheValue;

    protected abstract V checkValue();

    protected abstract boolean doSelfSwitch(V to);

    public void setCacheValue(V cacheValue) {
        this.cacheValue = cacheValue;
    }

    @Override
    public V getCurrentValue() {
        if (cacheValue == null) {
            cacheValue = checkValue();
        }
        return cacheValue;
    }

    @Override
    protected final boolean selfSwitch(V to) {
        if (doSelfSwitch(to)) {
            cacheValue = to;
            return true;
        }
        cacheValue = null;
        return false;
    }
}
