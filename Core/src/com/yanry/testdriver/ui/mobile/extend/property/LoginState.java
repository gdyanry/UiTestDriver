package com.yanry.testdriver.ui.mobile.extend.property;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.property.Property;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 5/11/2017.
 */
public class LoginState extends Property<Boolean> {
    private CurrentUser currentUser;

    public LoginState(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    protected boolean switchTo(Boolean to, List<Path> superPathContainer, Supplier<Boolean> finalCheck) {
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
