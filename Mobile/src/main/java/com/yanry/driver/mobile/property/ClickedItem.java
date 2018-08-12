package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.state.UnaryIntPredicate;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.view.ListView;
import com.yanry.driver.mobile.view.ListViewItem;

public class ClickedItem extends ViewProperty<ListViewItem> {
    private ListViewSize size;

    public ClickedItem(ListView view, ListViewSize size) {
        super(view);
        this.size = size;
    }

    @Override
    protected ListViewItem doCheckValue() {
        return null;
    }

    @Override
    protected boolean doSelfSwitch(ListViewItem to) {
        return size.switchTo(new UnaryIntPredicate(0, true)) || getGraph().performAction(new Click<>(to));
    }
}
