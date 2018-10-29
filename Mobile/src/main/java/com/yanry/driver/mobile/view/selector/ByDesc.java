package com.yanry.driver.mobile.view.selector;

import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

/**
 * Created by rongyu.yan on 4/21/2017.
 */
public class ByDesc extends ViewSelector {
    private String desc;

    public ByDesc(String desc) {
        this.desc = desc;
    }

    @Visible
    @EqualsPart
    public String getDesc() {
        return desc;
    }
}
