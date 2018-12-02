package com.yanry.driver.core.model.predicate;

import com.yanry.driver.core.model.base.ValuePredicate;

import java.util.stream.Stream;

public abstract class ActionPredicate<V> extends ValuePredicate<V> {

    public static <V> ActionPredicate<V> get(Runnable runnable) {
        return new ActionPredicate<V>() {
            @Override
            protected void run() {
                runnable.run();
            }
        };
    }

    private ActionPredicate() {
    }

    @Override
    public Stream<V> getConcreteValues() {
        return null;
    }

    @Override
    public boolean test(V value) {
        run();
        return true;
    }

    protected abstract void run();
}
