package com.yanry.driver.mobile.view.selector;

import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.mobile.view.View;

/**
 * Created by rongyu.yan on 4/27/2017.
 */
@Presentable
public class ByChild implements ViewSelector {
    private View child;

    public ByChild(View child) {
        this.child = child;
    }

    @Presentable
    public View getChild() {
        return child;
    }
}
