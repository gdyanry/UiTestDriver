package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.IntProperty;
import com.yanry.driver.core.model.expectation.ActionExpectation;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.core.model.runtime.fetch.NonNegativeIntegerQuery;
import com.yanry.driver.mobile.view.View;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

public abstract class ViewIntProperty extends IntProperty {
    private View view;

    public ViewIntProperty(View view) {
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

    @Override
    protected final Integer checkValue() {
        return getGraph().obtainValue(new NonNegativeIntegerQuery(this));
    }
}
