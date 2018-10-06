/**
 *
 */
package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.expectation.SDPropertyExpectation;
import com.yanry.driver.core.model.expectation.SSPropertyExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.state.Equals;
import lib.common.util.object.Presentable;

import java.util.Optional;
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

    public final ActionEvent switchToValue(V toState) {
        return switchTo(new Equals<>(toState));
    }

    public final ActionEvent switchTo(ValuePredicate<V> toState) {
        if (toState.test(getCurrentValue())) {
            return null;
        }
        if (toState.getValidValue() != null) {
            Optional<ActionEvent> any = toState.getValidValue().map(v -> doSelfSwitch(v)).filter(a -> a != null && graph.isValidAction(a)).findAny();
            if (any.isPresent()) {
                return any.get();
            }
        }
        return graph.findPathToRoll(e -> {
            if (e instanceof PropertyExpectation) {
                PropertyExpectation<Object> exp = (PropertyExpectation<Object>) e;
                return equals(exp.getProperty()) && toState.test((V) exp.getExpectedValue());
            }
            return false;
        });
    }

    public SSPropertyExpectation<V> getStaticExpectation(Timing timing, boolean needCheck, V value) {
        return new SSPropertyExpectation<>(timing, needCheck, this, value);
    }

    public SDPropertyExpectation<V> getDynamicExpectation(Timing timing, boolean needCheck, Supplier<V> valueSupplier) {
        return new SDPropertyExpectation<>(timing, needCheck, this, valueSupplier);
    }

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

    public final V getCurrentValue() {
        V cacheValue = (V) getGraph().propertyCache.get(this);
        if (cacheValue == null) {
            cacheValue = checkValue();
            getGraph().propertyCache.put(this, cacheValue);
        }
        return cacheValue;
    }

    protected abstract V checkValue();

    protected abstract ActionEvent doSelfSwitch(V to);
}
