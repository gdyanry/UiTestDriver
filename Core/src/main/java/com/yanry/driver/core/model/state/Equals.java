package com.yanry.driver.core.model.state;

import java.util.stream.Stream;

public class Equals<V> extends UnaryPredicate<V> {

    public Equals(V operand) {
        super(operand);
    }

    @Override
    public boolean test(V value) {
        return getOperand().equals(value);
    }

    @Override
    protected Stream<V> getValidValue() {
        return Stream.of(getOperand());
    }
}
