package com.yanry.driver.core.model.state;

import java.util.stream.Stream;

public abstract class ValueNotEquals<V> extends UnaryPredicate<V> {
    public ValueNotEquals(V operand) {
        super(operand);
    }

    @Override
    public boolean test(V value) {
        return !getOperand().equals(value);
    }

    @Override
    public Stream<V> getValidValue() {
        return getAllValues().filter(this::test);
    }

    protected abstract Stream<V> getAllValues();
}
