package com.yanry.driver.core.model.predicate;

/**
 * @Author: yanry
 * @Date: 2018/11/6 22:07
 */
public class GreaterThan<V extends Comparable<V>> extends UnaryPredicate<V> {
    public GreaterThan(V operand) {
        super(operand);
    }

    @Override
    public boolean test(V value) {
        return value != null && value.compareTo(getOperand()) > 0;
    }
}
