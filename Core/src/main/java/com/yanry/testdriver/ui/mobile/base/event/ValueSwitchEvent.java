/**
 *
 */
package com.yanry.testdriver.ui.mobile.base.event;

import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.Property;

/**
 * @author yanry
 *         <p>
 *         Jan 5, 2017
 */
@Presentable
public class ValueSwitchEvent<V> implements Event {
    private Property<V> property;
    private V from;
    private V to;

    public ValueSwitchEvent(Property<V> property, V from, V to) {
        this.property = property;
        this.from = from;
        this.to = to;
    }

    @Presentable
    public Property<V> getProperty() {
        return property;
    }

    @Presentable
    public V getFrom() {
        return from;
    }

    @Presentable
    public V getTo() {
        return to;
    }
}