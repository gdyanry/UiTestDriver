package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.property.Property;

/**
 * Created by rongyu.yan on 5/11/2017.
 */
public class LoginState extends Property<Boolean> {
    private CurrentUser currentUser;

    public LoginState(Graph graph, CurrentUser currentUser) {
        super(graph);
        this.currentUser = currentUser;
    }

    @Override
    public void handleExpectation(Boolean expectedValue, boolean needCheck) {

    }

    @Override
    protected boolean selfSwitch(Boolean to) {
        if (to) {
            return currentUser.getUserPasswordMap().keySet().stream().anyMatch(u -> currentUser.switchTo(u));
        }
        return currentUser.switchTo("");
    }

    @Override
    public Boolean getCurrentValue() {
        return !currentUser.getCurrentValue().equals("");
    }
}
