package com.yanry.driver.mobile.view.listview;

import com.yanry.driver.core.model.base.StateSpace;

public interface ListViewItemCreator<I extends ListViewItem<I>> {
    I create(StateSpace stateSpace, ListView<I> listView, int index);
}
