package com.yanry.testdriver.ui.mobile.extend.view.selector;

import com.yanry.testdriver.ui.mobile.base.Presentable;

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
