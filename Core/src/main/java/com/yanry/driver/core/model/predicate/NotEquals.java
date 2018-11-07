package com.yanry.driver.core.model.predicate;

public class NotEquals<V> extends UnaryPredicate<V> {
    public NotEquals(V operand) {
        super(operand);
    }

    @Override
    public boolean test(V value) {
        return !getOperand().equals(value);
    }
}
