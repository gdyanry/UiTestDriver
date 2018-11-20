package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.mobile.view.ViewContainer;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

public class ViewAction extends ExternalEvent {
    private ViewContainer view;

    public ViewAction(ViewContainer view) {
        this.view = view;
    }

    @EqualsPart
    @Visible
    public ViewContainer getView() {
        return view;
    }
}
