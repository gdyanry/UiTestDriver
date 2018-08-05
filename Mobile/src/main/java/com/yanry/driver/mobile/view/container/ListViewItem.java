package com.yanry.driver.mobile.view.container;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.ViewContainer;
import com.yanry.driver.mobile.view.selector.ViewSelector;

/**
 * Created by rongyu.yan on 4/27/2017.
 */
public class ListViewItem extends View implements ViewContainer {

    public ListViewItem(Graph graph, ViewContainer parent, ViewSelector selector) {
        super(graph, parent, selector);
    }
}
