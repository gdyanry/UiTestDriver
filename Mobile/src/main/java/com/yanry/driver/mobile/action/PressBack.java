package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ExternalEvent;
import lib.common.model.Singletons;

public class PressBack extends ExternalEvent {

    public static PressBack get() {
        return Singletons.get(PressBack.class);
    }
}
