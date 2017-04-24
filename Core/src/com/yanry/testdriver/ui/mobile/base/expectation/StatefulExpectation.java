package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.StateProperty;

import java.util.List;

/**
 * Created by rongyu.yan on 4/24/2017.
 */
public abstract class StatefulExpectation<V> extends AbstractExpectation {
    private StateProperty<V> property;
    private V value;

    public StatefulExpectation(Timing timing, StateProperty<V> property, V value) {
        super(timing);
        this.property = property;
        this.value = value;
    }

    @Presentable
    public StateProperty<V> getProperty() {
        return property;
    }

    @Presentable
    public V getValue() {
        return value;
    }

    @Override
    public boolean verify(List<Path> superPathContainer) {
        // this path might become transition event of other paths
        return getGraph().verifySuperPaths(property, property.getCurrentValue(),
                value, superPathContainer, () -> {
                    if (property.ifNeedVerification()) {
                        if (getGraph().verifyExpectation(this)) {
                            return true;
                        } else {
                            property.setCacheValue(null);
                            return false;
                        }
                    } else {
                        property.setCacheValue(value);
                        return true;
                    }
                });
    }

    @Override
    public boolean ifRecord() {
        return property.ifNeedVerification();
    }
}
