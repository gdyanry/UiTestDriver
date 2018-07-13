/**
 *
 */
package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.event.StateEvent;
import com.yanry.testdriver.ui.mobile.base.expectation.SDPropertyExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.SSPropertyExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;

import java.util.Objects;
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
    private long communicateTimeFrame;

    public Property(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return graph;
    }

    public final boolean switchTo(V to) {
        return to.equals(getCurrentValue())
                // 先尝试自转化再搜索是否存在可用路径
                || (verifySuperPaths(to)
                || graph.findPathToRoll((prop, val) -> equals(prop) && to.equals(val)))
                && to.equals(getCurrentValue());
    }

    private boolean verifySuperPaths(V to) {
        V currentValue = getCurrentValue();
        if (selfSwitch(to)) {
            graph.verifySuperPaths(this, currentValue, to);
            return true;
        }
        return false;
    }

    public StateEvent<V> getStateEvent(V from, V to) {
        return new StateEvent<>(this, from, to);
    }

    public SSPropertyExpectation<V> getStaticExpectation(Timing timing, boolean needCheck, V value) {
        return new SSPropertyExpectation<>(timing, needCheck, this, value);
    }

    public SDPropertyExpectation<V> getDynamicExpectation(Timing timing, boolean needCheck, Supplier<V> valueSupplier) {
        return new SDPropertyExpectation<>(timing, needCheck, this, valueSupplier);
    }

    public long getCommunicateTimeFrame() {
        return communicateTimeFrame;
    }

    public void setCommunicateTimeFrame(long communicateTimeFrame) {
        this.communicateTimeFrame = communicateTimeFrame;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !obj.getClass().equals(getClass())) {
            return false;
        }
        Property<V> property = (Property<V>) obj;
        return property.graph.equals(graph) && equalsWithSameClass(property);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(graph, getClass());
    }

    public abstract void handleExpectation(V expectedValue, boolean needCheck);

    public abstract V getCurrentValue();

    protected abstract boolean selfSwitch(V to);

    protected abstract boolean equalsWithSameClass(Property<V> property);
}
