package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.Property;

import java.util.List;
import java.util.function.Supplier;

/** A key-value pair (aka state) expectation
 * Created by rongyu.yan on 5/10/2017.
 */
public abstract class PropertyExpectation<V> extends Expectation {
    private Property<V> property;
    private V value;
    private Supplier<V> valueSupplier;

    public PropertyExpectation(Timing timing, Property<V> property, V value) {
        super(timing);
        this.property = property;
        this.value = value;
    }

    public PropertyExpectation(Timing timing, Property<V> property, Supplier<V> valueSupplier) {
        super(timing);
        this.property = property;
        this.valueSupplier = valueSupplier;
    }

    protected abstract boolean doSelfVerify();

    @Presentable
    public Property<V> getProperty() {
        return property;
    }

    @Presentable
    public V getValue() {
        return value;
    }

    @Override
    protected boolean selfVerify() {
        if (valueSupplier != null) {
            value = valueSupplier.get();
            boolean pass = doSelfVerify();
            value = null;
            return pass;
        }
        return doSelfVerify();
    }
}
