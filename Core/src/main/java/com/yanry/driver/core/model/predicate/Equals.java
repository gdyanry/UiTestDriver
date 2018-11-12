package com.yanry.driver.core.model.predicate;

import java.util.Objects;

public class Equals<V> extends UnaryPredicate<V> {

    public Equals(V operand) {
        super(operand);
    }

    @Override
    public boolean test(V value) {
        return Objects.equals(getOperand(), value);
    }
}
