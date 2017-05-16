package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.expectation.PropertyExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * state property that is supposed to be used as expectation of a path.
 * <p>
 * Created by rongyu.yan on 5/9/2017.
 */
public abstract class SearchableSwitchableProperty<V> extends CacheSwitchableProperty<V> {
    protected abstract Graph getGraph();

    @Override
    protected boolean switchTo(V to, List<Path> superPathContainer, Supplier<Boolean> finalCheck) {
        return getGraph().switchToState(this, to, superPathContainer, finalCheck);
    }

    protected abstract boolean ifNeedVerification();

    public SwitchablePropertyExpectation getStaticExpectation(Timing timing, V value) {
        return new SwitchablePropertyExpectation(timing, value);
    }

    public SwitchablePropertyExpectation getDynamicExpectation(Timing timing, Supplier<V> valueSupplier) {
        return new SwitchablePropertyExpectation(timing, valueSupplier);
    }

    public class SwitchablePropertyExpectation extends PropertyExpectation<V, SearchableSwitchableProperty<V>> implements Consumer<List<Path>> {

        private SwitchablePropertyExpectation(Timing timing, V value) {
            super(timing, SearchableSwitchableProperty.this, value);
        }

        private SwitchablePropertyExpectation(Timing timing, Supplier<V> valueSupplier) {
            super(timing, SearchableSwitchableProperty.this, valueSupplier);
        }

        @Override
        protected boolean doVerify(List<Path> superPathContainer) {
            // this path might become transition event of other paths
            return getGraph().verifySuperPaths(SearchableSwitchableProperty.this, getCurrentValue(),
                    getValue(), superPathContainer, () -> {
                        if (ifNeedVerification() && !getGraph().verifyExpectation(this)) {
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
            return ifNeedVerification();
        }

        @Override
        public void accept(List<Path> paths) {
            if (ifNeedVerification()) {
                throw new IllegalStateException("the property's ifNeedVerification() method should return false when " +
                        "used as following action.");
            }
            verify(paths);
        }
    }
}
