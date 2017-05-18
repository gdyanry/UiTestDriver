/**
 *
 */
package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;

import java.util.List;
import java.util.function.Supplier;

/**
 * Property that can do transition between its values. Direct subclasses are not supposed to be used as an
 * expectation in a path, meaning that the state transition of this property is accomplished by realizing the
 * {@link #switchTo(Object, List, Supplier)} method instead of searching paths from the graph.
 *
 * @author yanry
 *         <p>
 *         Jan 5, 2017
 */
@Presentable
public abstract class Property<V> {

    public boolean switchTo(V to, List<Path> superPathContainer) {
        if (to.equals(getCurrentValue())) {
            return true;
        }
        return switchTo(to, superPathContainer, () -> to.equals(getCurrentValue()));
    }

    protected abstract boolean switchTo(V to, List<Path> superPathContainer, Supplier<Boolean> finalCheck);

    public abstract V getCurrentValue();
}
