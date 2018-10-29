package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.IntProperty;
import com.yanry.driver.core.model.runtime.fetch.NonNegativeIntegerQuery;
import com.yanry.driver.mobile.view.View;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

public class ViewIntProperty extends IntProperty {
    private View view;

    public ViewIntProperty(Graph graph, View view) {
        super(graph);
        this.view = view;
    }

    @Visible
    @EqualsPart
    public View getView() {
        return view;
    }

    @Override
    protected final Integer checkValue() {
        if (view.getCurrentValue()) {
            return getGraph().obtainValue(new NonNegativeIntegerQuery(this));
        }
        return null;
    }
}
