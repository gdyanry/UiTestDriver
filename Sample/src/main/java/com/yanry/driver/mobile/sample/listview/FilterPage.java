package com.yanry.driver.mobile.sample.listview;

import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.mobile.window.Application;
import com.yanry.driver.mobile.window.Window;

public class FilterPage extends Window {
    public FilterPage(StateSpace stateSpace, Application manager) {
        super(stateSpace, manager);
    }

    @Override
    protected void addCases(StateSpace stateSpace, Application manager) {
        closeOnPressBack();
    }
}
