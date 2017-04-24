package com.yanry.testdriver.ui.mobile.extend.property;

import com.yanry.testdriver.ui.mobile.base.*;
import com.yanry.testdriver.ui.mobile.base.StateProperty;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by rongyu.yan on 3/3/2017.
 */
public class GeneralProperty<V> extends StateProperty<V> {
    private Graph graph;
    private boolean ifNeedVerification;
    private V[] optionValues;

    public GeneralProperty(Graph graph, boolean ifNeedVerification, V... optionValues) {
        this.graph = graph;
        this.ifNeedVerification = ifNeedVerification;
        this.optionValues = optionValues;
    }

    @Override
    public boolean transitTo(Predicate<V> to, List<Path> superPathContainer) {
        return getGraph().transitToState(this, to, superPathContainer);
    }

    @Override
    public V checkValue() {
        return getGraph().checkState(new StateToCheck<>(this, optionValues));
    }

    @Override
    protected Graph getGraph() {
        return graph;
    }

    @Override
    public boolean ifNeedVerification() {
        return ifNeedVerification;
    }
}
