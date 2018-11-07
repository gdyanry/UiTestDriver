package com.yanry.driver.core.model.predicate;

public class Equals<V> extends UnaryPredicate<V> {

    public Equals(V operand) {
        super(operand);
    }

    @Override
    public boolean test(V value) {
        return getOperand().equals(value);
    }
}
