package com.yanry.driver.mobile.view.selector;

import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

/**
 * Created by rongyu.yan on 4/21/2017.
 */
public class ByText extends ViewSelector {
    private String text;

    public ByText(String text) {
        this.text = text;
    }

    @Visible
    @EqualsPart
    public String getText() {
        return text;
    }
}
