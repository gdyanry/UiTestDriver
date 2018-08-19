package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.mobile.window.Window;

/**
 * Created by rongyu.yan on 4/19/2017.
 */
@Presentable
public class ClickOutside<R> extends ActionEvent<ClickOutside<R>, Window, R> {

    public ClickOutside(Window data) {
        super(data);
    }
}
