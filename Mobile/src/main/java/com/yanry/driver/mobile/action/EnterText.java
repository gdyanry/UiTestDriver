package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.mobile.view.EditText;

/**
 * Created by rongyu.yan on 2/28/2017.
 */
@Presentable
public class EnterText<R> extends ActionEvent<EditText, R> {
    private String text;

    public EnterText(EditText view, String text) {
        super(view);
        this.text = text;
    }

    @Presentable
    public String getText() {
        return text;
    }
}
