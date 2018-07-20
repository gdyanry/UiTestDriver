package com.yanry.driver.mobile.view.selector;

import com.yanry.driver.core.model.runtime.Presentable;

/**
 * Created by rongyu.yan on 4/21/2017.
 */
@Presentable
public class ByDesc implements ViewSelector {
    private String desc;

    public ByDesc(String desc) {
        this.desc = desc;
    }

    @Presentable
    public String getDesc() {
        return desc;
    }
}