package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Graph;

/**
 * state property that is supposed to be used as expectation of a path.
 * <p>
 * Created by rongyu.yan on 5/9/2017.
 */
public abstract class CacheProperty<V> extends Property<V> {

    public CacheProperty(Graph graph) {
        super(graph);
    }

    protected abstract V checkValue();

    protected abstract boolean doSelfSwitch(V to);

    @Override
    public final void handleExpectation(V expectedValue, boolean needCheck) {
        if (expectedValue != null && needCheck) {
            if (!getGraph().isValueFresh(this)) {
                // 清空缓存，使得接下来调用getCurrentValue时触发向客户端查询并更新该属性最新的状态值
                getGraph().setCacheValue(this, null);
            }
        } else {
            // 不查询客户端，直接通过验证并更新状态值
            getGraph().setCacheValue(this, expectedValue);
        }
    }

    @Override
    public final V getCurrentValue() {
        V cacheValue = getGraph().getCacheValue(this);
        if (cacheValue == null) {
            cacheValue = checkValue();
            getGraph().setCacheValue(this, cacheValue);
        }
        return cacheValue;
    }

    @Override
    protected final boolean selfSwitch(V to) {
        if (doSelfSwitch(to)) {
            handleExpectation(to, false);
            return true;
        }
        return false;
    }
}
