package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.mobile.view.selector.ByIndex;

public abstract class ListViewItem extends View {
    public ListViewItem(Graph graph, ViewContainer parent, int index) {
        super(graph, parent, new ByIndex(index));
    }

    protected abstract void fetchViewPropertyValues();
}
