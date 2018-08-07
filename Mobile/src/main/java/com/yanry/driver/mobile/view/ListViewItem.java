package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.ViewContainer;
import com.yanry.driver.mobile.view.selector.ByIndex;
import com.yanry.driver.mobile.view.selector.ViewSelector;

/**
 * Created by rongyu.yan on 4/27/2017.
 */
public class ListViewItem extends View {

    public ListViewItem(Graph graph, ViewContainer parent, int index) {
        super(graph, parent, new ByIndex(index));
    }
}
