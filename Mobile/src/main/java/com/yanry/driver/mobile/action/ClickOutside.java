package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.mobile.WindowManager;

/**
 * Created by rongyu.yan on 4/19/2017.
 */
@Presentable
public class ClickOutside<R> extends ActionEvent<WindowManager.Window, R> {

    public ClickOutside(WindowManager.Window data) {
        super(data);
    }
}
