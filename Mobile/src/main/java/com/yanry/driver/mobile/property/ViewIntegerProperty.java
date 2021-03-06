package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.IntegerProperty;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.core.model.runtime.fetch.IntegerQuery;
import com.yanry.driver.mobile.view.View;
import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.Visible;

public abstract class ViewIntegerProperty extends IntegerProperty {
    private View view;

    public ViewIntegerProperty(View view) {
        super(view.getStateSpace());
        this.view = view;
        addDependentState(view, Equals.of(true));
        view.addOnCleanListener(() -> cleanCache());
    }

    @Visible
    @EqualsPart
    public View getView() {
        return view;
    }

    @Override
    protected final Integer checkValue(Integer expected) {
        return getStateSpace().obtainValue(new IntegerQuery(this), expected);
    }
}
