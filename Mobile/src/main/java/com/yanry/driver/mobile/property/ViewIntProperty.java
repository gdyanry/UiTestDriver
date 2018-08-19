package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.IntProperty;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.core.model.runtime.StateToCheck;
import com.yanry.driver.mobile.view.View;

public class ViewIntProperty extends IntProperty {
    private View view;

    public ViewIntProperty(Graph graph, View view) {
        super(graph);
        this.view = view;
    }

    @Presentable
    public View getView() {
        return view;
    }

    @Override
    protected final Integer checkValue() {
        if (view.getCurrentValue()) {
            return getGraph().checkState(new StateToCheck<>(this));
        }
        return null;
    }
}
