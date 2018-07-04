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

import java.util.function.Supplier;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
@Presentable
public abstract class Property<V> {
    private Graph graph;

    public Property(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return graph;
    }

    public final boolean switchTo(V to, boolean verifySuperPaths) {
        return to.equals(getCurrentValue()) ||
                // 先搜索是否存在可用路径
                (graph.findPathToRoll((prop, val) -> equals(prop) && to.equals(val), verifySuperPaths) ||
                        // 若无可用路径再尝试自转化
                        verifySuperPaths(to, verifySuperPaths))
                        && to.equals(getCurrentValue());
    }

    private boolean verifySuperPaths(V to, boolean verifySuperPaths) {
        V currentValue = getCurrentValue();
        boolean selfSwitch = selfSwitch(to);
        if (selfSwitch && verifySuperPaths) {
            graph.verifySuperPaths(this, currentValue, to);
        }
        return selfSwitch;
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

    public abstract void handleExpectation(V expectedValue, boolean needCheck);

    public abstract V getCurrentValue();

    protected abstract boolean selfSwitch(V to);

    protected abstract boolean equalsWithSameClass(Property<V> property);
}
