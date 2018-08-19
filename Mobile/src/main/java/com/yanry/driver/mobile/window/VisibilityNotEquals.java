package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.state.ValueNotEquals;

import java.util.stream.Stream;

public class VisibilityNotEquals extends ValueNotEquals<Visibility> {
    public VisibilityNotEquals(Visibility operand) {
        super(operand);
    }

    @Override
    protected Stream<Visibility> getAllValues() {
        return Stream.of(Visibility.values()).filter(visibility -> visibility != getOperand());
    }
}
