package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.ActionFilter;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.runtime.fetch.StringQuery;
import com.yanry.driver.mobile.view.View;

import java.util.Set;
import java.util.stream.Stream;

public class Text extends ViewProperty<String> {
    public Text(View view) {
        super(view);
    }

    @Override
    protected String checkValue(String expected) {
        return getStateSpace().obtainValue(new StringQuery(this), expected);
    }

    @Override
    protected ExternalEvent doSelfSwitch(String to, ActionFilter actionFilter) {
        return null;
    }

    @Override
    protected Stream<String> getValueStream(Set<String> collectedValues) {
        return collectedValues.stream();
    }
}
