package com.yanry.driver.core.model.state;

import com.yanry.driver.core.model.base.ValuePredicate;
import lib.common.util.object.HashAndEquals;
import lib.common.util.object.Presentable;

public abstract class UnaryPredicate<V> extends ValuePredicate<V> {
    private V operand;

    public UnaryPredicate(V operand) {
        this.operand = operand;
    }

    @HashAndEquals
    @Presentable
    public V getOperand() {
        return operand;
    }
}
