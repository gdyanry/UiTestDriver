package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ActionEvent;
import lib.common.model.Singletons;

public class ClickLauncher extends ActionEvent<ClickLauncher, Object, Object> {
    public static ClickLauncher get() {
        return Singletons.get(ClickLauncher.class);
    }
}
