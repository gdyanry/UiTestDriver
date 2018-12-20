package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.mobile.view.View;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

public abstract class ViewProperty<V> extends Property<V> {
    private View view;

    public ViewProperty(View view) {
        super(view.getStateSpace());
        this.view = view;
        setDependentStates(view.getState(Equals.of(true)));
        view.addOnCleanListener(() -> cleanCache());
    }

    @Visible
    @EqualsPart
    public View getView() {
        return view;
    }
}
