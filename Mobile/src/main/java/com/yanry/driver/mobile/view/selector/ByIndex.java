package com.yanry.driver.mobile.view.selector;

import com.yanry.driver.core.model.runtime.Presentable;

/**
 * Created by rongyu.yan on 4/27/2017.
 */
@Presentable
public class ByIndex implements ViewSelector {
    private int index;

    public ByIndex(int index) {
        this.index = index;
    }

    @Presentable
    public int getIndex() {
        return index;
    }
}
