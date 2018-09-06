package com.yanry.driver.core.model.state;

import com.yanry.driver.core.model.runtime.Presentable;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class UnaryIntPredicate extends UnaryPredicate<Integer> implements Supplier<Integer> {
    private boolean isLargerThan;
    private int temp;

    public UnaryIntPredicate(Integer operand, boolean isLargerThan) {
        super(operand);
        this.isLargerThan = isLargerThan;
        temp = operand;
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
}
