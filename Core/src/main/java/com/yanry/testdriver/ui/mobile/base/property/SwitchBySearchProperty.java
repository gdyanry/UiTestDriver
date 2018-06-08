package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.expectation.PropertyExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

/**
 * state property that is supposed to be used as expectation of a path.
 * <p>
 * Created by rongyu.yan on 5/9/2017.
 */
public abstract class SwitchBySearchProperty<V> extends CacheProperty<V> {

    protected abstract boolean isVisibleToUser();

    public SwitchablePropertyExpectation getExpectation(Timing timing, V value) {
        return new SwitchablePropertyExpectation(timing, value);
    }

    public SwitchablePropertyExpectation getExpectation(Timing timing, Supplier<V> valueSupplier) {
        return new SwitchablePropertyExpectation(timing, valueSupplier);
    }

    public class SwitchablePropertyExpectation extends PropertyExpectation<V, SwitchBySearchProperty<V>> {

        private SwitchablePropertyExpectation(Timing timing, V value) {
            super(timing, SwitchBySearchProperty.this, value);
        }

        private SwitchablePropertyExpectation(Timing timing, Supplier<V> valueSupplier) {
            super(timing, SwitchBySearchProperty.this, valueSupplier);
        }

        public boolean isSatisfied(BiPredicate<SwitchBySearchProperty, Object> endStatePredicate) {
            return endStatePredicate.test(SwitchBySearchProperty.this, getValue());
        }

        @Override
        protected boolean doSelfVerify() {
            // this path might become transition event of other paths
            return getGraph().verifySuperPaths(SwitchBySearchProperty.this, getCurrentValue(),
                    getValue(), () -> {
                        if (isVisibleToUser() && !getGraph().verifyExpectation(this)) {
                            setCacheValue(null);
                            return false;
                        } else {
                            setCacheValue(getValue());
                            return true;
                        }
                    });
        }

        @Override
        public boolean ifRecord() {
            return isVisibleToUser();
        }
    }
}
