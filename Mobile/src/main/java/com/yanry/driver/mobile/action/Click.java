package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.mobile.view.View;

/**
 * Created by rongyu.yan on 2/18/2017.
 */
@Presentable
public class Click<R> extends ActionEvent<Click<R>, View, R> {

    public Click(View target) {
        super(a -> a.getTarget());
        setTarget(target);
    }
}
