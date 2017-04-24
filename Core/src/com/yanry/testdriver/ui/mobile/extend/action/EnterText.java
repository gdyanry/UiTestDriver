package com.yanry.testdriver.ui.mobile.extend.action;

import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.event.ActionEvent;
import com.yanry.testdriver.ui.mobile.extend.view.EditText;

/**
 * Created by rongyu.yan on 2/28/2017.
 */
@Presentable
public class EnterText implements ActionEvent {
    private EditText view;
    private String text;

    public EnterText(EditText view, String text) {
        this.view = view;
        this.text = text;
    }

    @Presentable
    public EditText getView() {
        return view;
    }

    @Presentable
    public String getText() {
        return text;
    }
}
