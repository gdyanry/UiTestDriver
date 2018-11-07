package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.predicate.CompoundPredicate;

/**
 * @Author: yanry
 * @Date: 2018/11/6 23:33
 */
public class And<V> extends CompoundPredicate<V> {

    public And(ValuePredicate<V>... predicates) {
        super(predicates);
    }

    @Override
    public boolean test(V value) {
        for (ValuePredicate<V> predicate : getPredicates()) {
            if (!predicate.test(value)) {
                return false;
            }
        }
        return true;
    }
}
