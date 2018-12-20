package com.yanry.driver.mobile.view.listview;

import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByIndex;

public abstract class ListViewItem<I extends ListViewItem<I>> extends View {
    public ListViewItem(StateSpace stateSpace, ListView<I> parent, int index) {
        super(stateSpace, parent, new ByIndex(index));
    }

    protected abstract void queryViewStates();
}
