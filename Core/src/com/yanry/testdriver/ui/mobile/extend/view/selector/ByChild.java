package com.yanry.testdriver.ui.mobile.extend.view.selector;

import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.extend.view.View;

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
