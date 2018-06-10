/**
 *
 */
package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.expectation.StaticPropertyExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
@Presentable
public abstract class Property<V> {

    public final boolean switchTo(Graph graph, V to) {
        return to.equals(getCurrentValue(graph)) ||
                // 自身的状态转化可能会触发执行别的路径
                graph.verifySuperPaths(this, getCurrentValue(graph), to, () ->
                        // 先搜索是否存在可用路径
                        graph.findPathToRoll(null, (prop, val) -> equals(prop) && to.equals(val)) ||
                                // 若无可用路径再尝试自转化
                                selfSwitch(graph, to))
                        && to.equals(getCurrentValue(graph));
    }

    public StaticPropertyExpectation<V> getExpectation(Timing timing, V value) {
        return new StaticPropertyExpectation<>(timing, this, value);
    }

    protected abstract boolean selfSwitch(Graph graph, V to);

    public abstract V getCurrentValue(Graph graph);

    public abstract boolean isCheckedByUser();
}
