package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ExpectationEvent;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.property.EditText;
import lib.common.util.object.HashAndEquals;
import lib.common.util.object.Presentable;

/**
 * Created by rongyu.yan on 2/28/2017.
 */
public class EnterText extends ExpectationEvent {
    private EditText editText;
    private String text;

    public EnterText(EditText editText, String text) {
        super(editText.getStaticExpectation(Timing.IMMEDIATELY, false, text));
        this.editText = editText;
        this.text = text;
    }

    @HashAndEquals
    @Presentable
    public EditText getEditText() {
        return editText;
    }

    @HashAndEquals
    @Presentable
    public String getText() {
        return text;
    }

}
