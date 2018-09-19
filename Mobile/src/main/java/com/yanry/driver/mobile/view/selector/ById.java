package com.yanry.driver.mobile.view.selector;

import lib.common.util.object.Presentable;

@Presentable
public class ById implements ViewSelector {
    private String id;

    public ById(String id) {
        this.id = id;
    }

    @Presentable
    public String getId() {
        return id;
    }
}
