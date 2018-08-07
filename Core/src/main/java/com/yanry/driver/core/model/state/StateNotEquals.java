package com.yanry.driver.core.model.state;

import java.util.stream.Stream;

public abstract class StateNotEquals<V> extends UnaryPredicate<V> {
    public StateNotEquals(V operand) {
        super(operand);
    }

    @Override
    public boolean test(V value) {
        return !getOperand().equals(value);
    }

    @Override
    public Stream<V> getValidValue() {
        return getAllValues().filter(v -> test(v));
    }

    protected abstract Stream<V> getAllValues();
}
