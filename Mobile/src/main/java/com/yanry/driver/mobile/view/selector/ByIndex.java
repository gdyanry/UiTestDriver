package com.yanry.driver.mobile.view.selector;

import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

/**
 * Created by rongyu.yan on 4/27/2017.
 */
public class ByIndex extends ViewSelector {
    private int index;

    public ByIndex(int index) {
        this.index = index;
    }

    @EqualsPart
    @Visible
    public int getIndex() {
        return index;
    }
}
