/**
 *
 */
package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;

/**
 * Property that can do transition between its values. Direct subclasses are not supposed to be used as an
 * expectation in a path, meaning that the state transition of this property is accomplished by realizing the
 * {@link #selfSwitch(Object)} method instead of searching paths from the graph.
 *
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
@Presentable
public abstract class Property<V> {

    public boolean switchTo(V to) {
        return to.equals(getCurrentValue()) ||
                // 自身的状态转化可能会触发执行别的路径
                getGraph().verifySuperPaths(this, getCurrentValue(), to, () ->
                        // 先搜索是否存在可用路径
                        getGraph().findPathToRoll(null, (prop, val) -> equals(prop) && to.equals(val)) ||
                                // 若无可用路径再尝试自转化
                                selfSwitch(to))
                        && to.equals(getCurrentValue());
    }

    protected abstract Graph getGraph();

    protected abstract boolean selfSwitch(V to);

    public abstract V getCurrentValue();
}
