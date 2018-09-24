package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.mobile.view.ViewContainer;
import lib.common.util.object.HashAndEquals;
import lib.common.util.object.Presentable;

/**
 * Created by rongyu.yan on 4/19/2017.
 */
@Presentable
public class ClickOutside extends ActionEvent {
    private ViewContainer viewContainer;

    public ClickOutside(ViewContainer viewContainer) {
        this.viewContainer = viewContainer;
    }

    @HashAndEquals
    @Presentable
    public ViewContainer getViewContainer() {
        return viewContainer;
    }
}
