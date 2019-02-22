package com.yanry.driver.mobile.view.listview;

import com.yanry.driver.core.model.base.ActionFilter;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.mobile.property.ViewProperty;

import java.util.Set;
import java.util.stream.Stream;

public class ClickedItem<I extends ListViewItem<I>> extends ViewProperty<I> {

    public ClickedItem(ListView<I> view) {
        super(view);
    }

    @Override
    protected I checkValue() {
        return null;
    }

    @Override
    protected ExternalEvent doSelfSwitch(I to, ActionFilter actionFilter) {
        return null;
    }

    @Override
    protected Stream<I> getValueStream(Set<I> collectedValues) {
        return collectedValues.stream();
    }
}
