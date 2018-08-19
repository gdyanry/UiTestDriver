package com.yanry.driver.core.model.state;

import java.util.stream.Stream;

public interface ValuePredicate<V> {
    boolean test(V value);

    Stream<V> getValidValue();
}
