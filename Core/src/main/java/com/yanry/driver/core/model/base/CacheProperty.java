package com.yanry.driver.core.model.base;

/**
 * state property that is supposed to be used as expectation of a path.
 * <p>
 * Created by rongyu.yan on 5/9/2017.
 */
public abstract class CacheProperty<V> extends Property<V> {

    public CacheProperty(Graph graph) {
        super(graph);
    }

    @Override
    public final void handleExpectation(V expectedValue, boolean needCheck) {
        if (expectedValue != null && needCheck) {
            if (!getGraph().isValueFresh(this)) {
                // 清空缓存，使得接下来调用getCurrentValue时触发向客户端查询并更新该属性最新的状态值
                getGraph().propertyCache.put(this, null);
            }
        } else {
            // 不查询客户端，直接通过验证并更新状态值
            getGraph().propertyCache.put(this, expectedValue);
        }
    }

    @Override
    public final V getCurrentValue() {
        V cacheValue = (V) getGraph().propertyCache.get(this);
        if (cacheValue == null) {
            cacheValue = checkValue();
            getGraph().propertyCache.put(this, cacheValue);
        }
        return cacheValue;
    }

    @Override
    protected final boolean selfSwitch(V to) {
        SwitchResult switchResult = doSelfSwitch(to);
        // 注意此时不是通过搜寻路径来实现状态变迁的，故触发动作后需要处理缓存值。
        switch (switchResult) {
            case NoAction:
                return false;
            case ActionNoCheck:
                handleExpectation(to, false);
                break;
            case ActionNeedCheck:
                handleExpectation(to, true);
                break;
        }
        return true;
    }

    protected abstract V checkValue();

    /**
     * @param to
     * @return 是否触发ActionEvent
     */
    protected abstract SwitchResult doSelfSwitch(V to);

    public enum SwitchResult {
        NoAction, ActionNoCheck, ActionNeedCheck
    }
}
