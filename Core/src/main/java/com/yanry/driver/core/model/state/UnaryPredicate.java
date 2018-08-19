package com.yanry.driver.core.model.state;

import com.yanry.driver.core.model.runtime.Presentable;

@Presentable
public abstract class UnaryPredicate<V> implements ValuePredicate<V> {
    private V operand;

    public UnaryPredicate(V operand) {
        this.operand = operand;
    }

    @Presentable
    public V getOperand() {
        return operand;
    }
}
