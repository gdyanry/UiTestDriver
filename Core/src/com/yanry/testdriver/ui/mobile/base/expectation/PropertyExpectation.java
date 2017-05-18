package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.Property;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 5/10/2017.
 */
public abstract class PropertyExpectation<V, P extends Property<V>> extends AbstractExpectation {
    private P property;
    private V value;
    private Supplier<V> valueSupplier;

    public PropertyExpectation(Timing timing, P property, V value) {
        super(timing);
        this.property = property;
        this.value = value;
    }

    public PropertyExpectation(Timing timing, P property, Supplier<V> valueSupplier) {
        super(timing);
        this.property = property;
        this.valueSupplier = valueSupplier;
    }

    protected abstract boolean doVerify(List<Path> superPathContainer);

    @Presentable
    public P getProperty() {
        return property;
    }

    @Presentable
    public V getValue() {
        return value;
    }

    @Override
    protected boolean verify(List<Path> superPathContainer) {
        if (valueSupplier != null) {
            value = valueSupplier.get();
            boolean pass = doVerify(superPathContainer);
            value = null;
            return pass;
        }
        return doVerify(superPathContainer);
    }
}
