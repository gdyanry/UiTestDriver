package com.yanry.testdriver.ui.mobile.extend.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.property.Property;

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
    protected boolean equalsWithSameClass(Property<Boolean> property) {
        return true;
    }

    @Override
    public Boolean getCurrentValue() {
        return !currentUser.getCurrentValue().equals("");
    }
}
