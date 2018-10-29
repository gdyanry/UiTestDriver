package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.mobile.view.View;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

/**
 * Created by rongyu.yan on 2/18/2017.
 */
public class Click extends ExternalEvent {
    private View view;

    public Click(View view) {
        this.view = view;
    }

    @EqualsPart
    @Visible
    public View getView() {
        return view;
    }
}
