package com.yanry.testdriver.ui.mobile.base.event;

import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.Property;

import java.util.function.Predicate;

/**
 * Created by rongyu.yan on 5/17/2017.
 */
@Presentable
public class PassiveSwitchEvent<V, P extends Property<V>> implements Event {
    private P property;
    private Predicate<V> from;
    private Predicate<V> to;

    public PassiveSwitchEvent(P property, Predicate<V> from, Predicate<V> to) {
        this.property = property;
        this.from = from;
        this.to = to;
    }

    @Presentable
    public P getProperty() {
        return property;
    }

    public Predicate<V> getFrom() {
        return from;
    }

    public Predicate<V> getTo() {
        return to;
    }
}
