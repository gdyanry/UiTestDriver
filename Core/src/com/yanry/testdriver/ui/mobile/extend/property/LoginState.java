package com.yanry.testdriver.ui.mobile.extend.property;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.property.SwitchableProperty;

import java.util.List;

/**
 * Created by rongyu.yan on 5/11/2017.
 */
public class LoginState extends SwitchableProperty<Boolean> {
    private CurrentUser currentUser;

    public LoginState(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    protected boolean doSwitch(Boolean to, List<Path> superPathContainer) {
        if (to) {
            return currentUser.getUserPasswordMap().keySet().stream().anyMatch(u -> currentUser.switchTo(u,
                    superPathContainer));
        }
        return currentUser.switchTo("", superPathContainer);
    }

    @Override
    public Boolean getCurrentValue() {
        return !currentUser.getCurrentValue().equals("");
    }
}
