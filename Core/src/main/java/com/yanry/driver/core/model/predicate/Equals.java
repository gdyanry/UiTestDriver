package com.yanry.driver.core.model.predicate;

import java.util.HashMap;
import java.util.Objects;

public class Equals<V> extends UnaryPredicate<V> {
    private static HashMap<Object, Equals<?>> cache = new HashMap<>();

    public static <V> Equals<V> of(V value) {
        Equals<?> equals = cache.get(value);
        if (equals == null) {
            equals = new Equals<>(value);
            cache.put(value, equals);
        }
        return (Equals<V>) equals;
    }

    private Equals(V operand) {
        super(operand);
    }

    @Override
    public boolean test(V value) {
        return Objects.equals(getOperand(), value);
    }
}
