/**
 *
 */
package com.yanry.testdriver.ui.mobile.base;

import com.yanry.testdriver.ui.mobile.base.expectation.StatefulExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author yanry
 *         <p>
 *         Jan 5, 2017
 */
@Presentable
public abstract class StateProperty<V> {
    private V cacheValue;

    public V getCurrentValue() {
        if (cacheValue == null) {
            cacheValue = checkValue();
        }
        return cacheValue;
    }

    public void setCacheValue(V cacheValue) {
        this.cacheValue = cacheValue;
    }

    public StatefulExpectation<V> getExpectation(Timing timing, V value) {
        return new StatefulExpectation(timing, this, value) {
            @Override
            protected Graph getGraph() {
                return StateProperty.this.getGraph();
            }
        };
    }

    public abstract boolean transitTo(Predicate<V> to, List<Path> superPathContainer);

    protected abstract V checkValue();

    protected abstract Graph getGraph();

    public abstract boolean ifNeedVerification();

}
