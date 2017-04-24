package com.yanry.testdriver.ui.mobile.extend.action;

import com.yanry.testdriver.ui.mobile.base.event.ActionEvent;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.extend.window.Window;

/**
 * Created by rongyu.yan on 4/19/2017.
 */
@Presentable
public class ClickOutside implements ActionEvent {
    private Window window;

    public ClickOutside(Window window) {
        this.window = window;
    }

    @Presentable
    public Window getWindow() {
        return window;
    }
}
