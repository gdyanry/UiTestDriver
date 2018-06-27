package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Graph;

/**
 * state property that is supposed to be used as expectation of a path.
 * <p>
 * Created by rongyu.yan on 5/9/2017.
 */
public abstract class CacheProperty<V> extends Property<V> {
    private V cacheValue;

    protected abstract V checkValue(Graph graph);

    protected abstract boolean doSelfSwitch(Graph graph, V to);

    public abstract boolean isCheckedByUser();

    public void setCacheValue(V cacheValue) {
        this.cacheValue = cacheValue;
    }

    @Override
    public final V getCurrentValue(Graph graph) {
        if (cacheValue == null) {
            cacheValue = checkValue(graph);
        }
        return cacheValue;
    }

    @Override
    protected final boolean selfSwitch(Graph graph, V to) {
        if (doSelfSwitch(graph, to)) {
            cacheValue = to;
            return true;
        }
        return false;
    }
}
