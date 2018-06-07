/**
 *
 */
package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;

import java.util.List;

/**
 * Property that can do transition between its values. Direct subclasses are not supposed to be used as an
 * expectation in a path, meaning that the state transition of this property is accomplished by realizing the
 * {@link #doSwitch(Object)} method instead of searching paths from the graph.
 *
 * @author yanry
 *         <p>
 *         Jan 5, 2017
 */
@Presentable
public abstract class Property<V> {

    public boolean switchTo(V to) {
        if (to.equals(getCurrentValue())) {
            return true;
        }
        return doSwitch(to) && to.equals(getCurrentValue());
    }

    protected abstract boolean doSwitch(V to);

    public abstract V getCurrentValue();
}
