/**
 *
 */
package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.event.StateEvent;
import com.yanry.testdriver.ui.mobile.base.expectation.DynamicPropertyExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.StaticPropertyExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;

import java.util.function.Supplier;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
@Presentable
public abstract class Property<V> {

    public final boolean switchTo(Graph graph, V to, boolean verifySuperPaths) {
        return to.equals(getCurrentValue(graph)) ||
                // 先搜索是否存在可用路径
                (graph.findPathToRoll(null, (prop, val) -> equals(prop) && to.equals(val), verifySuperPaths) ||
                        // 若无可用路径再尝试自转化
                        verifySuperPaths(graph, to, verifySuperPaths))
                        && to.equals(getCurrentValue(graph));
    }

    private boolean verifySuperPaths(Graph graph, V to, boolean verifySuperPaths) {
        V currentValue = getCurrentValue(graph);
        boolean selfSwitch = selfSwitch(graph, to);
        if (selfSwitch && verifySuperPaths) {
            graph.verifySuperPaths(this, currentValue, to);
        }
        return selfSwitch;
    }

    public StateEvent<V> getStateEvent(V from, V to) {
        return new StateEvent<>(this, from, to);
    }

    public StaticPropertyExpectation<V> getStaticExpectation(Timing timing, boolean needCheck, V value) {
        return new StaticPropertyExpectation<>(timing, needCheck, this, value);
    }

    public DynamicPropertyExpectation<V> getDynamicExpectation(Timing timing, boolean needCheck, Supplier<V> valueSupplier) {
        return new DynamicPropertyExpectation<>(timing, needCheck, this, valueSupplier);
    }

    public abstract void handleExpectation(V expectedValue, boolean needCheck);

    protected abstract boolean selfSwitch(Graph graph, V to);

    public abstract V getCurrentValue(Graph graph);

}
