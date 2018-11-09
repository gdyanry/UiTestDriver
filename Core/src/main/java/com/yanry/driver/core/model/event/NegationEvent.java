package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.TransitionEvent;
import com.yanry.driver.core.model.base.ValuePredicate;

public class NegationEvent<V> extends TransitionEvent<V> {
    public NegationEvent(Property<V> property, ValuePredicate<V> from) {
        super(property, from, from.not());
    }
}
