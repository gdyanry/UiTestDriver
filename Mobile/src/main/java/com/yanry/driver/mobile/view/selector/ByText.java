package com.yanry.driver.mobile.view.selector;

import com.yanry.driver.core.model.runtime.Presentable;

/**
 * Created by rongyu.yan on 4/21/2017.
 */
@Presentable
public class ByText implements ViewSelector {
    private String text;

    public ByText(String text) {
        this.text = text;
    }

    @Presentable
    public String getText() {
        return text;
    }
}
