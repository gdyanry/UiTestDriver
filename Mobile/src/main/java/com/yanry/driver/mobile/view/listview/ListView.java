package com.yanry.driver.mobile.view.listview;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.event.Event;
import com.yanry.driver.core.model.event.StateChangeCallback;
import com.yanry.driver.core.model.event.TransitionEvent;
import com.yanry.driver.core.model.expectation.ActionExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.state.Equals;
import com.yanry.driver.core.model.state.NotEquals;
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
        clickItemEvent = new TransitionEvent<>(clickedItem, null, new NotEquals<>(itemNone) {
            @Override
            protected Stream<I> getAllValues() {
                return items.keySet().stream().filter(pos -> pos < size.getCurrentValue()).map(pos -> items.get(pos));
            }
        });
        // 变为可见时清除clickedItem缓存
        getWindow().createForegroundPath(getShowEvent(), clickedItem.getStaticExpectation(Timing.IMMEDIATELY, false, itemNone));
        // size变化时重新初始化item
        getWindow().createForegroundPath(new StateChangeCallback<>(size, null, s -> s != null), new ActionExpectation() {
            @Override
            protected void run() {
                initItems();
            }
        }).addContextState(this, true);
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
            getWindow().createForegroundPath(new Click<>(child), clickedItem.getStaticExpectation(Timing.IMMEDIATELY, false, child))
                    .addContextState(this, true)
                    .addContextStatePredicate(size, new UnaryIntPredicate(index, true));
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
