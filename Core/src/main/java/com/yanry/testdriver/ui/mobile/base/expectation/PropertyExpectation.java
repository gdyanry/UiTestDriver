package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.property.Property;

/**
 * A key-value pair (aka state) expectation
 * Created by rongyu.yan on 5/10/2017.
 */
public abstract class PropertyExpectation<V> extends Expectation {
    private Property<V> property;

    public PropertyExpectation(Timing timing, Property<V> property) {
        super(timing);
        this.property = property;
    }

    @Presentable
    protected abstract V getExpectedValue();

    @Override
    protected final boolean selfVerify(Graph graph) {
        V expectedValue = getExpectedValue();
        return graph.verifySuperPaths(property, property.getCurrentValue(graph), expectedValue, () -> {
            if (property instanceof CacheProperty) {
                CacheProperty<V> cacheProperty = (CacheProperty<V>) property;
                if (cacheProperty.isCheckedByUser()) {
                    // 清空缓存，使得接下来调用getCurrentValue时触发向客户端查询并更新该属性最新的状态值
                    cacheProperty.setCacheValue(null);
                } else {
                    // 不查询客户端，直接通过验证并更新状态值
                    cacheProperty.setCacheValue(expectedValue);
                    return true;
                }
            }
            return expectedValue.equals(property.getCurrentValue(graph));
        });
    }

    @Presentable
    public Property<V> getProperty() {
        return property;
    }
}
