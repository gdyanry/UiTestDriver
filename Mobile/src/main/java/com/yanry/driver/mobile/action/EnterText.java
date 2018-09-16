package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.mobile.view.View;
import lib.common.model.EqualsProxy;

/**
 * Created by rongyu.yan on 2/28/2017.
 */
@Presentable
public class EnterText extends ActionEvent<View, Object> {
    private String text;
    private EqualsProxy<EnterText> equalsProxy;

    public EnterText(View view, String text) {
        super(view);
        this.text = text;
        equalsProxy = new EqualsProxy<>(this, e -> e.getTarget(), e -> e.text);
    }

    @Presentable
    public String getText() {
        return text;
    }

}
