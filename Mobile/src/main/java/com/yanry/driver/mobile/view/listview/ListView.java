package com.yanry.driver.mobile.view.listview;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.event.Event;
import com.yanry.driver.core.model.event.StateChangeCallback;
import com.yanry.driver.core.model.event.StateEvent;
import com.yanry.driver.core.model.expectation.ActionExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.state.ValueEquals;
import com.yanry.driver.core.model.state.ValueNotEquals;
import com.yanry.driver.core.model.state.UnaryIntPredicate;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.ViewContainer;
import com.yanry.driver.mobile.view.selector.ViewSelector;
import lib.common.model.Singletons;

import java.util.HashMap;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by rongyu.yan on 4/25/2017.
 */
public class ListView<I extends ListViewItem<I>> extends View {
    private ListViewSize size;
    private HashMap<Integer, I> items;
    private ClickedItem<I> clickedItem;
    private I itemNone;
    private Event clickItemEvent;
    private ListViewItemCreator<I> itemCreator;

    public ListView(Graph graph, ViewContainer parent, ViewSelector selector, ListViewItemCreator<I> itemCreator) {
        super(graph, parent, selector);
        this.itemCreator = itemCreator;
        size = new ListViewSize(graph, this);
        items = new HashMap<>();
        clickedItem = new ClickedItem(this);
        itemNone = itemCreator.create(graph, this, -1);
        clickItemEvent = new StateEvent<>(clickedItem, new ValueEquals<>(itemNone), new ValueNotEquals<I>(itemNone) {
            @Override
            protected Stream<I> getAllValues() {
                return items.keySet().stream().filter(key -> key < size.getCurrentValue()).map(key -> items.get(key));
            }
        });
        // 变为可见时清除clickedItem缓存
        getWindow().createPath(getShowEvent(), clickedItem.getStaticExpectation(Timing.IMMEDIATELY, false, itemNone));
        // size变化时重新初始化item
        getWindow().createPath(new StateChangeCallback<>(size, null, s -> s != null), new ActionExpectation() {
            @Override
            protected void run() {
                initItems();
            }
        }).addInitState(this, true);
    }

    public void initItems() {
        if (getCurrentValue()) {
            int listSize = size.getCurrentValue();
            for (int i = 0; i < listSize; i++) {
                getViewByIndex(i).fetchViewPropertyValues();
            }
        }
    }

    private I getViewByIndex(int index) {
        I child = items.get(index);
        if (child == null) {
            child = itemCreator.create(getGraph(), this, index);
            getWindow().createPath(new Click<>(child), clickedItem.getStaticExpectation(Timing.IMMEDIATELY, false, child))
                    .addInitState(this, true)
                    .addInitStatePredicate(size, new UnaryIntPredicate(index, true));
            items.put(index, child);
        }
        return child;
    }

    public ListViewSize getSize() {
        return size;
    }

    public ClickedItem<I> getClickedItem() {
        return clickedItem;
    }

    public Event getClickItemEvent() {
        return clickItemEvent;
    }

    public Supplier<I> getItem(IntFunction<Integer> index) {
        return () -> {
            int iSize = size.getCurrentValue();
            if (iSize > 0) {
                return getViewByIndex(index.apply(iSize));
            }
            return null;
        };
    }

    public Supplier<I> getRandomItem() {
        return getItem(size -> Singletons.get(Random.class).nextInt(size));
    }

    public Supplier<I> getItemByFilter(Predicate<I> filter) {
        return () -> {
            int iSize = size.getCurrentValue();
            for (int i = 0; i < iSize; i++) {
                I item = getViewByIndex(i);
                if (filter.test(item)) {
                    return item;
                }
            }
            return null;
        };
    }
}
