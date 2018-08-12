package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.base.Event;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.mobile.property.ClickedItem;
import com.yanry.driver.mobile.property.ListViewSize;
import com.yanry.driver.mobile.view.selector.ByIndex;
import com.yanry.driver.mobile.view.selector.ViewSelector;
import lib.common.model.Singletons;

import java.util.HashMap;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 4/25/2017.
 */
public abstract class ListView<I extends ListViewItem> extends View {
    private ListViewSize size;
    private HashMap<Integer, ListViewItem> items;
    private ClickedItem clickedItem;

    public ListView(Graph graph, ViewContainer parent, ViewSelector selector) {
        super(graph, parent, selector);
        size = new ListViewSize(graph, this);
        items = new HashMap<>();
        clickedItem = new ClickedItem(this, size);
    }

    private View getViewByIndex(int index) {
        View child = items.get(index);
        if (child == null) {
            child = new View(getGraph(), this, new ByIndex(index));
            items.put(index, getItem(index));
        }
        return child;
    }

    public ListViewSize getSize() {
        return size;
    }

    public ClickedItem getClickedItem() {
        return clickedItem;
    }

    public Event getClickItemEvent() {
        return clickedItem.getStateEvent(null, )
    }

    public Supplier<View> getItem(IntFunction<Integer> index) {
        return () -> {
            int iSize = size.getCurrentValue();
            if (iSize > 0) {
                return getViewByIndex(index.apply(iSize));
            }
            return null;
        };
    }

    public Supplier<View> getRandomItem() {
        return getItem(size -> Singletons.get(Random.class).nextInt(size));
    }

    public Supplier<View> getItemByFilter(Predicate<View> filter) {
        return () -> {
            int iSize = size.getCurrentValue();
            for (int i = 0; i < iSize; i++) {
                View item = getViewByIndex(i);
                if (filter.test(item)) {
                    return item;
                }
            }
            return null;
        };
    }

    protected abstract I getItem(int index);
}
