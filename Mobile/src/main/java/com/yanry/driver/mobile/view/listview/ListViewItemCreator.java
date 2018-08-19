package com.yanry.driver.mobile.view.listview;

import com.yanry.driver.core.model.base.Graph;

public interface ListViewItemCreator<I extends ListViewItem<I>> {
    I create(Graph graph, ListView<I> listView, int index);
}
