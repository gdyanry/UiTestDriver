package com.yanry.driver.mobile.view.selector;

import com.yanry.driver.mobile.view.View;
import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.Visible;

/**
 * Created by rongyu.yan on 4/27/2017.
 */
@Visible
public class ByChild extends ViewSelector {
    private View child;

    public ByChild(View child) {
        this.child = child;
    }

    @Visible
    @EqualsPart
    public View getChild() {
        return child;
    }
}
