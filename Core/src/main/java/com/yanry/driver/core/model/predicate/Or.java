package com.yanry.driver.core.model.predicate;

/**
 * @Author: yanry
 * @Date: 2018/11/6 23:35
 */
public class Or<V> extends CompoundPredicate<V> {

    public Or(ValuePredicate<V>... predicates) {
        super(predicates);
    }

    @Override
    public boolean test(V value) {
        for (ValuePredicate<V> predicate : getPredicates()) {
            if (predicate.test(value)) {
                return true;
            }
        }
        return false;
    }
}
