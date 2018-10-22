package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ExternalEvent;
import lib.common.model.Singletons;

public class ClickLauncher extends ExternalEvent {

    public static ClickLauncher get() {
        return Singletons.get(ClickLauncher.class);
    }
}
