package com.yanry.driver.core.model.predicate;

import com.yanry.driver.core.model.base.ValuePredicate;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.stream.Stream;

public abstract class UnaryPredicate<V> extends ValuePredicate<V> {
    private V operand;

    public UnaryPredicate(V operand) {
        this.operand = operand;
    }

    @EqualsPart
    @Visible
    public V getOperand() {
        return operand;
    }

    @Override
    public Stream<V> getConcreteValues() {
        return Stream.of(operand);
    }
}
