package com.yanry.driver.mobile.view.listview;

import com.yanry.driver.mobile.property.ViewProperty;

public class ClickedItem<I extends ListViewItem<I>> extends ViewProperty<I> {

    public ClickedItem(ListView<I> view) {
        super(view);
    }

    @Override
    protected I doCheckValue() {
        return null;
    }

    @Override
    protected SwitchResult doSelfSwitch(I to) {
        return SwitchResult.NoAction;
    }
}
