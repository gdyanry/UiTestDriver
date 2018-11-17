package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.TransitionEvent;
import com.yanry.driver.core.model.base.ValuePredicate;

import java.util.Objects;
import java.util.stream.Stream;

public class StateChangeEvent<V> extends TransitionEvent<V> {
    public StateChangeEvent(Property<V> property) {
        super(property, new ValuePredicate<V>() {
            @Override
            public Stream<V> getConcreteValues() {
                return null;
            }

            @Override
            public boolean test(V value) {
                return true;
            }
        }, new ValuePredicate<V>() {
            @Override
            public Stream<V> getConcreteValues() {
                return null;
            }

            @Override
            public boolean test(V value) {
                return !Objects.equals(value, property.getCurrentValue());
            }
        });
    }
}
