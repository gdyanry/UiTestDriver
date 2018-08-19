package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.mobile.view.View;

import java.util.ArrayList;

/**
 * Created by rongyu.yan on 2/28/2017.
 */
@Presentable
public class EnterText extends ActionEvent<EnterText, View, Object> {
    private String text;

    public EnterText(View view, String text) {
        super(view);
        this.text = text;
    }

    @Presentable
    public String getText() {
        return text;
    }

    @Override
    protected void addHashFields(ArrayList<Object> hashFields) {
        super.addHashFields(hashFields);
        hashFields.add(text);
    }

    @Override
    protected boolean equalsWithSameClass(EnterText object) {
        return super.equalsWithSameClass(object) && text.equals(object.text);
    }
}
