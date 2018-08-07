package com.yanry.driver.core.model.state;

import com.yanry.driver.core.model.runtime.Presentable;

import java.util.stream.Stream;

@Presentable
public class StateEquals<V> extends UnaryPredicate<V> {

    public StateEquals(V operand) {
        super(operand);
    }

    @Override
    public boolean test(V value) {
        return getOperand().equals(value);
    }

    @Override
    public Stream<V> getValidValue() {
        return Stream.of(getOperand());
    }
}
