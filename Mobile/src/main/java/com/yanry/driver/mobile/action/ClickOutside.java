package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.mobile.view.ViewContainer;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

/**
 * Created by rongyu.yan on 4/19/2017.
 */
public class ClickOutside extends ExternalEvent {
    private ViewContainer viewContainer;

    public ClickOutside(ViewContainer viewContainer) {
        this.viewContainer = viewContainer;
    }

    @EqualsPart
    @Visible
    public ViewContainer getViewContainer() {
        return viewContainer;
    }
}
