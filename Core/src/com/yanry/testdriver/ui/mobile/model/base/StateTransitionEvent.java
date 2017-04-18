/**
 *
 */
package com.yanry.testdriver.ui.mobile.model.base;

import java.util.function.Predicate;

/**
 * @author yanry
 *         <p>
 *         Jan 5, 2017
 */
@Presentable
public class StateTransitionEvent<V> implements Event {
    private ObjectProperty<V> property;
    private V to;
    private Predicate<V> from;

    public StateTransitionEvent(ObjectProperty<V> property, V to, Predicate<V> from) {
        this.property = property;
        this.to = to;
        this.from = from;
    }

    @Presentable
    public ObjectProperty<V> getProperty() {
        return property;
    }

    @Presentable
    public V getTo() {
        return to;
    }

    public Predicate<V> getFrom() {
        return from;
    }
}
