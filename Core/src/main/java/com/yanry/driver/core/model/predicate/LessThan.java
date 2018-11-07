package com.yanry.driver.core.model.predicate;

/**
 * @Author: yanry
 * @Date: 2018/11/6 22:10
 */
public class LessThan<V extends Comparable<V>> extends UnaryPredicate<V> {
    public LessThan(V operand) {
        super(operand);
    }

    @Override
    public boolean test(V value) {
        return value.compareTo(getOperand()) < 0;
    }
}
