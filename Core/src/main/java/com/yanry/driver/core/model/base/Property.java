/**
 *
 */
package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.expectation.SDPropertyExpectation;
import com.yanry.driver.core.model.expectation.SSPropertyExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.state.Equals;
import lib.common.util.object.Presentable;

import java.util.function.Supplier;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
@Presentable
public abstract class Property<V> {
    private Graph graph;

    /**
     * 缓存向客户端查询属性值时的graph的actionTimeFrame，用于防止非必要的重复查询。
     */
    long communicateTimeFrame;

    public Property(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return graph;
    }

    public final boolean switchToValue(V toState) {
        return switchTo(new Equals<>(toState));
    }

    /**
     * @param toState
     * @return 是否触发ActionEvent
     */
    public final boolean switchTo(ValuePredicate<V> toState) {
        return !toState.test(getCurrentValue()) &&
                // 先尝试自转化再搜索是否存在可用路径
                (verifySuperPaths(toState) || graph.findPathToRoll((prop, val) -> equals(prop) && toState.test((V) val)));
    }

    private boolean verifySuperPaths(ValuePredicate<V> toState) {
        V oldValue = getCurrentValue();
        return toState.getValidValue().anyMatch(v -> {
            SwitchResult switchResult = doSelfSwitch(v);
            // 注意此时不是通过搜寻路径来实现状态变迁的，故触发动作后需要处理缓存值。
            switch (switchResult) {
                case NoAction:
                    return false;
                case ActionNoCheck:
                    handleExpectation(v, false);
                    break;
                case ActionNeedCheck:
                    handleExpectation(v, true);
                    break;
            }
            V newValue = getCurrentValue();
            if (!newValue.equals(oldValue)) {
                graph.verifySuperPaths(this, oldValue, newValue);
            }
            return true;
        });
    }

    public SSPropertyExpectation<V> getStaticExpectation(Timing timing, boolean needCheck, V value) {
        return new SSPropertyExpectation<>(timing, needCheck, this, value);
    }

    public SDPropertyExpectation<V> getDynamicExpectation(Timing timing, boolean needCheck, Supplier<V> valueSupplier) {
        return new SDPropertyExpectation<>(timing, needCheck, this, valueSupplier);
    }

    public void handleExpectation(V expectedValue, boolean needCheck) {
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

    public V getCurrentValue() {
        V cacheValue = (V) getGraph().propertyCache.get(this);
        if (cacheValue == null) {
            cacheValue = checkValue();
            getGraph().propertyCache.put(this, cacheValue);
        }
        return cacheValue;
    }

    protected abstract V checkValue();

    protected abstract SwitchResult doSelfSwitch(V to);

    public enum SwitchResult {
        NoAction, ActionNoCheck, ActionNeedCheck
    }
}
