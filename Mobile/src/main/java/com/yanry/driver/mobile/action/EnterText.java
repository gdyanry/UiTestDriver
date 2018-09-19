package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.mobile.view.View;
import lib.common.util.object.HashAndEquals;
import lib.common.util.object.Presentable;

/**
 * Created by rongyu.yan on 2/28/2017.
 */
@Presentable
public class EnterText extends ActionEvent<View, Object> {
    private String text;

    public EnterText(View view, String text) {
        super(view);
        this.text = text;
    }

    @HashAndEquals
    @Presentable
    public String getText() {
        return text;
    }

}
