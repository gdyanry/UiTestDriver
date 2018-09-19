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

    /**
     * @param toState
     * @return 是否触发ActionEvent
     */
    public final boolean switchTo(ValuePredicate<V> toState) {
        return !toState.test(getCurrentValue()) &&
                // 先尝试自转化再搜索是否存在可用路径
                (verifySuperPaths(toState) || graph.findPathToRoll((prop, val) -> equals(prop) && toState.test((V) val)));
    }

    public final boolean switchToValue(V toState) {
        return switchTo(new Equals<>(toState));
    }

    private boolean verifySuperPaths(ValuePredicate<V> toState) {
        V oldValue = getCurrentValue();
        return toState.getValidValue().anyMatch(v -> {
            if (selfSwitch(v)) {
                V newValue = getCurrentValue();
                if (!newValue.equals(oldValue)) {
                    graph.verifySuperPaths(this, oldValue, newValue);
                }
                return true;
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

    public abstract void handleExpectation(V expectedValue, boolean needCheck);

    public abstract V getCurrentValue();

    /**
     * @param to
     * @return 是否触发ActionEvent
     */
    protected abstract boolean selfSwitch(V to);
}
