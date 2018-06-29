package com.yanry.testdriver.ui.mobile.extend.action;

import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.event.ActionEvent;
import com.yanry.testdriver.ui.mobile.extend.WindowManager;

/**
 * Created by rongyu.yan on 4/19/2017.
 */
@Presentable
public class ClickOutside<R> extends ActionEvent<WindowManager.Window, R> {

    public ClickOutside(WindowManager.Window data) {
        super(data);
    }
}
