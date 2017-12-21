package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.expectation.DynamicExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;

import java.util.List;

/**
 * Property that does not switch through searching the graph.
 * <p>
 * Created by rongyu.yan on 5/12/2017.
 */
public abstract class SwitchBySelfProperty<V> extends CacheProperty<V> {

    protected abstract boolean doSwitch(V to);

    protected abstract Graph getGraph();

    public Expectation getExpectation(V to) {
        return new DynamicExpectation() {
            @Override
            protected boolean selfVerify(List<Path> superPathContainer) {
                return getGraph().verifySuperPaths(SwitchBySelfProperty.this, getCurrentValue(), to, superPathContainer,
                        () -> {
                            setCacheValue(to);
                            return true;
                        });
            }
        };
    }

    @Override
    protected boolean doSwitch(V to, List<Path> parentPaths) {
        return getGraph().verifySuperPaths(this, getCurrentValue(), to, parentPaths, () -> {
            if (doSwitch(to)) {
                setCacheValue(to);
                return true;
            }
            setCacheValue(null);
            return false;
        });
    }
}
