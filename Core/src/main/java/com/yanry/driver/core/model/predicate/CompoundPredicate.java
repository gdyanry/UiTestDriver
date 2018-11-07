package com.yanry.driver.core.model.predicate;

import com.yanry.driver.core.model.base.ValuePredicate;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.HashSet;

/**
 * @Author: yanry
 * @Date: 2018/11/6 22:19
 */
public abstract class CompoundPredicate<V> extends ValuePredicate<V> {
    private HashSet<ValuePredicate<V>> predicates;

    public CompoundPredicate(ValuePredicate<V>... predicates) {
        for (ValuePredicate<V> predicate : predicates) {
            this.predicates.add(predicate);
        }
    }

    @EqualsPart
    @Visible
    public HashSet<ValuePredicate<V>> getPredicates() {
        return predicates;
    }
}
