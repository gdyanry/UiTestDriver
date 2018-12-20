package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.GlobalExternalEvent;

public final class GlobalActions {
    private GlobalActions() {
    }

    public static GlobalExternalEvent clickLauncher() {
        return GlobalExternalEvent.get("clickLauncher");
    }

    public static GlobalExternalEvent pressBack() {
        return GlobalExternalEvent.get("pressBack");
    }
}
