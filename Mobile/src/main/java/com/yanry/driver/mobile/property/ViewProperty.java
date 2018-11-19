package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.expectation.ActionExpectation;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.mobile.view.View;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

public abstract class ViewProperty<V> extends Property<V> {
    private View view;

    public ViewProperty(View view) {
        super(view.getGraph());
        this.view = view;
        setDependentStates(view.getState(Equals.of(true)));
        getGraph().createPath(view.getWindow().getCloseEvent(), new ActionExpectation() {
            @Override
            protected void run() {
                clean();
            }
        });
    }

    @Visible
    @EqualsPart
    public View getView() {
        return view;
    }
}
