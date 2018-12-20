package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.StateSpace;

public class NoWindow extends Window {
    public NoWindow(StateSpace stateSpace, Application manager) {
        super(stateSpace, manager);
    }

    @Override
    protected void addCases(StateSpace stateSpace, Application manager) {

    }
}
