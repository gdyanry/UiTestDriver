package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.mobile.view.View;
import lib.common.util.object.Presentable;

/**
 * Created by rongyu.yan on 2/18/2017.
 */
@Presentable
public class Click<R> extends ActionEvent<View, R> {

    public Click(View target) {
        super(target);
    }
}
