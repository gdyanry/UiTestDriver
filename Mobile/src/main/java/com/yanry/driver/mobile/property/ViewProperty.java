package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.mobile.view.View;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

public abstract class ViewProperty<V> extends Property<V> {
    private View view;

    public ViewProperty(View view) {
        super(view.getGraph());
        this.view = view;
    }

    @Visible
    @EqualsPart
    public View getView() {
        return view;
    }

    @Override
    protected final V checkValue() {
        if (view.getCurrentValue()) {
            return doCheckValue();
        }
        return null;
    }

    protected abstract V doCheckValue();
}
