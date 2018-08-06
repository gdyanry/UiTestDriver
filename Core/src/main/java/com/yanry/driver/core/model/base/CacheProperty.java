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
                getGraph().cacheProperties.put(this, null);
            }
        } else {
            // 不查询客户端，直接通过验证并更新状态值
            getGraph().cacheProperties.put(this, expectedValue);
        }
    }

    @Override
    public final V getCurrentValue() {
        V cacheValue = (V) getGraph().cacheProperties.get(this);
        if (cacheValue == null) {
            cacheValue = checkValue();
            getGraph().cacheProperties.put(this, cacheValue);
        }
        return cacheValue;
    }

    @Override
    protected final boolean selfSwitch(V to) {
        if (doSelfSwitch(to)) {
            // 注意此时不是通过搜寻路径来实现状态变迁的，故触发动作后需要处理缓存值。
            handleExpectation(to, needCheckAfterSelfSwitch());
            return true;
        }
        return false;
    }

    /**
     * 默认是认为{@link #doSelfSwitch(Object)}触发了必然导致状态变迁为期望值的某动作（ActionEvent），此时直接修改缓存值为预期值。
     *
     * @return 若不符合上述默认情形，则重新此方法并返回true
     */
    protected boolean needCheckAfterSelfSwitch() {
        return false;
    }

    protected abstract V checkValue();

    /**
     * @param to
     * @return 是否触发ActionEvent
     */
    protected abstract boolean doSelfSwitch(V to);
}
