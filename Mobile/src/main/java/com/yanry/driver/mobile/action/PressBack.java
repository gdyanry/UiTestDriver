package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ActionEvent;
import lib.common.model.Singletons;

public class PressBack extends ActionEvent<PressBack, Object, Object> {
    public static PressBack get() {
        return Singletons.get(PressBack.class);
    }
}
