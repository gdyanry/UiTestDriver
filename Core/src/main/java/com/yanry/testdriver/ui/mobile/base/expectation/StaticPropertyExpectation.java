package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.Property;

import java.util.function.BiPredicate;

public class StaticPropertyExpectation<V> extends PropertyExpectation<V> {
    private V value;

    public StaticPropertyExpectation(Timing timing, Property<V> property, V value) {
        super(timing, property);
        this.value = value;
    }

    public boolean isSatisfied(BiPredicate<Property<V>, V> predicate) {
        return predicate.test(getProperty(), value);
    }

    @Presentable
    public V getValue() {
        return value;
    }

    @Override
    protected final boolean selfVerify(Graph graph) {
        return !getProperty().isCheckedByUser() || graph.verifyExpectation(this);
    }

    @Override
    public final boolean ifRecord() {
        return getProperty().isCheckedByUser();
    }
}
