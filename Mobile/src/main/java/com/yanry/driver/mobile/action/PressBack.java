package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ActionEvent;
import lib.common.model.Singletons;

import java.util.ArrayList;

public class PressBack extends ActionEvent {
    public PressBack() {
        super(null);
    }

    public static PressBack get() {
        return Singletons.get(PressBack.class);
    }
}
