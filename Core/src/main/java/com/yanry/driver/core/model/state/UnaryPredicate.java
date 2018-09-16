package com.yanry.driver.core.model.state;

import com.yanry.driver.core.model.runtime.Presentable;
import lib.common.model.EqualsProxy;

@Presentable
public abstract class UnaryPredicate<V> implements ValuePredicate<V> {
    private V operand;
    private EqualsProxy<UnaryPredicate<V>> equalsProxy;

    public UnaryPredicate(V operand) {
        this.operand = operand;
        equalsProxy = new EqualsProxy<>(this, p -> p.operand);
    }

    @Presentable
    public V getOperand() {
        return operand;
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
