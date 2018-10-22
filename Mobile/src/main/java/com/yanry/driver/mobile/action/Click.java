package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ExternalEvent;
import com.yanry.driver.mobile.view.View;
import lib.common.util.object.HashAndEquals;
import lib.common.util.object.Presentable;

/**
 * Created by rongyu.yan on 2/18/2017.
 */
public class Click<R> extends ExternalEvent {
    private View view;

    public Click(View view) {
        this.view = view;
    }

    @HashAndEquals
    @Presentable
    public View getView() {
        return view;
    }
}
