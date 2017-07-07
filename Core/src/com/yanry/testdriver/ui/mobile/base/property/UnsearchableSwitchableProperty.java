package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.expectation.ActionExpectation;

import java.util.List;

/**
 * SwitchableProperty that does not switch through searching the graph.
 * <p>
 * Created by rongyu.yan on 5/12/2017.
 */
public abstract class UnsearchableSwitchableProperty<V> extends CacheSwitchableProperty<V> {

    protected abstract boolean doSwitch(V to);

    protected abstract Graph getGraph();

    public ActionExpectation getActionExpectation(V to) {
        return new ActionExpectation() {
            @Override
            protected void run(List<Path> superPathContainer) {
                getGraph().verifySuperPaths(UnsearchableSwitchableProperty.this, getCurrentValue(), to, superPathContainer,
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
