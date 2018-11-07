package com.yanry.driver.core.model.base;

import lib.common.util.object.EqualsPart;
import lib.common.util.object.HandyObject;
import lib.common.util.object.Visible;

public abstract class ValuePredicate<V> extends HandyObject {
    public ValuePredicate<V> not() {
        if (this instanceof ValuePredicate.Not) {
            return ((Not) this).getPredicate();
        }
        return new Not();
    }

    public abstract boolean test(V value);

    public class Not extends ValuePredicate<V> {

        @Visible
        @EqualsPart
        public ValuePredicate<V> getPredicate() {
            return ValuePredicate.this;
        }

        @Override
        public boolean test(V value) {
            return !ValuePredicate.this.test(value);
        }
    }
}
