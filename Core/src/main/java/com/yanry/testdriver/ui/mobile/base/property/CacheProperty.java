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

    @Override
    public final void handleExpectation(V expectedValue, boolean needCheck) {
        if (expectedValue != null && needCheck) {
            // 清空缓存，使得接下来调用getCurrentValue时触发向客户端查询并更新该属性最新的状态值
            this.cacheValue = null;
        } else {
            // 不查询客户端，直接通过验证并更新状态值
            this.cacheValue = expectedValue;
        }
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
            handleExpectation(to, false);
            return true;
        }
        return false;
    }
}
