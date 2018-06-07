package com.yanry.testdriver.ui.mobile.extend.property;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.property.Property;

import java.util.List;

/**
 * Created by rongyu.yan on 5/11/2017.
 */
public class LoginState extends Property<Boolean> {
    private CurrentUser currentUser;

    public LoginState(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    protected boolean doSwitch(Boolean to) {
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
