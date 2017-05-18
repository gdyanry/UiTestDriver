package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.expectation.ActionExpectation;

import java.util.List;
import java.util.function.Supplier;

/**
 * Property that does not switch through searching the graph.
 * <p>
 * Created by rongyu.yan on 5/12/2017.
 */
public abstract class UnsearchableProperty<V> extends CacheProperty<V> {

    protected abstract boolean doSwitch(V to, List<Path> superPathContainer, Supplier<Boolean> finalCheck);

    protected abstract Graph getGraph();

    public ActionExpectation getActionExpectation(V to) {
        return new ActionExpectation() {
            @Override
            protected void run(List<Path> superPathContainer) {
                getGraph().verifySuperPaths(UnsearchableProperty.this, getCurrentValue(), to, superPathContainer,
                        () -> {
                            setCacheValue(to);
                            return true;
                        });
            }
        };
    }

    @Override
    protected boolean switchTo(V to, List<Path> superPathContainer, Supplier<Boolean> finalCheck) {
        return getGraph().verifySuperPaths(this, getCurrentValue(), to, superPathContainer, () -> {
            if (doSwitch(to, superPathContainer, finalCheck)) {
                setCacheValue(to);
                return true;
            }
            setCacheValue(null);
            return false;
        });
    }
}
