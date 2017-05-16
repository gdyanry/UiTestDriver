package com.yanry.testdriver.ui.mobile.extend.view.container;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;

/**
 * Created by rongyu.yan on 4/27/2017.
 */
public class ListViewItem extends View implements ViewContainer {

    public ListViewItem(ListView parent, ViewSelector selector) {
        super(parent, selector);
    }

    @Override
    public void present(Path path) {
        getParent().present(path);
    }
}
