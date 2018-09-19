package com.yanry.driver.core.model.state;

import java.util.stream.Stream;

public abstract class NotEquals<V> extends UnaryPredicate<V> {
    public NotEquals(V operand) {
        super(operand);
    }

    @Override
    public boolean test(V value) {
        return !getOperand().equals(value);
    }

    @Override
    protected Stream<V> getValidValue() {
        return getAllValues().filter(this::test);
    }

    protected abstract Stream<V> getAllValues();
}
