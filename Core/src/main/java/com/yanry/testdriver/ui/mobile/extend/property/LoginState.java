package com.yanry.testdriver.ui.mobile.extend.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.property.Property;

/**
 * Created by rongyu.yan on 5/11/2017.
 */
public class LoginState extends Property<Boolean> {
    private CurrentUser currentUser;

    public LoginState(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    protected boolean selfSwitch(Graph graph, Boolean to) {
        if (to) {
            return currentUser.getUserPasswordMap().keySet().stream().anyMatch(u -> currentUser.switchTo(graph, u));
        }
        return currentUser.switchTo(graph, "");
    }

    @Override
    public Boolean getCurrentValue(Graph graph) {
        return !currentUser.getCurrentValue(graph).equals("");
    }

    @Override
    public boolean isCheckedByUser() {
        return false;
    }
}
