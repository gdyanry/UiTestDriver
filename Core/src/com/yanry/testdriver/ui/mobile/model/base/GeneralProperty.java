package com.yanry.testdriver.ui.mobile.model.base;

import com.yanry.testdriver.ui.mobile.model.base.Graph;
import com.yanry.testdriver.ui.mobile.model.base.ObjectProperty;
import com.yanry.testdriver.ui.mobile.model.base.StateToCheck;
import com.yanry.testdriver.ui.mobile.model.base.Timing;

/**
 * Created by rongyu.yan on 3/3/2017.
 */
public class GeneralProperty<V> extends ObjectProperty<V> {
    private Graph graph;
    private V[] optionValues;

    public GeneralProperty(boolean needVerification, Graph graph, V... optionValues) {
        super(needVerification);
        this.graph = graph;
        this.optionValues = optionValues;
    }

    @Override
    public V checkValue(Timing timing) {
        return graph.checkState(new StateToCheck<>(this, optionValues, timing));
    }
}
