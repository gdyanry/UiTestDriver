package com.yanry.driver.core.model.state;

import com.yanry.driver.core.model.runtime.Presentable;
import lib.common.model.EqualsProxy;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class UnaryIntPredicate extends UnaryPredicate<Integer> implements Supplier<Integer> {
    private boolean isLargerThan;
    private int temp;
    private EqualsProxy<UnaryIntPredicate> equalsProxy;

    public UnaryIntPredicate(Integer operand, boolean isLargerThan) {
        super(operand);
        this.isLargerThan = isLargerThan;
        temp = operand;
        equalsProxy = new EqualsProxy<>(this, e -> e.getOperand(), e -> e.isLargerThan);
    }

    @Presentable
    public boolean isLargerThan() {
        return isLargerThan;
    }

    @Override
    public boolean test(Integer value) {
        return value == null ? false : isLargerThan ? value > getOperand() : value < getOperand();
    }

    @Override
    public Stream<Integer> getValidValue() {
        return Stream.generate(this);
    }

    @Override
    public Integer get() {
        return isLargerThan ? ++temp : --temp;
    }

    @Override
    public int hashCode() {
        return equalsProxy.getHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return equalsProxy.checkEquals(obj);
    }
}
