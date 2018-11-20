package com.yanry.driver.mobile.view.listview;

import com.yanry.driver.core.model.BooleanProperty;
import com.yanry.driver.core.model.base.Event;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.TransitionEvent;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.predicate.GreaterThan;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.ViewContainer;
import com.yanry.driver.mobile.view.selector.ViewSelector;
import lib.common.model.Singletons;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.HashMap;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 4/25/2017.
 */
public class ListView<I extends ListViewItem<I>> extends View {
    private ListViewSize size;
    private HashMap<Integer, I> items;
    private ClickedItem<I> clickedItem;
    private Event clickItemEvent;
    private ListViewItemCreator<I> itemCreator;
    private ItemClick itemClick;

    public ListView(Graph graph, ViewContainer parent, ViewSelector selector, ListViewItemCreator<I> itemCreator) {
        super(graph, parent, selector);
        this.itemCreator = itemCreator;
        size = new ListViewSize(this);
        items = new HashMap<>();
        clickedItem = new ClickedItem(this);
        itemClick = new ItemClick(graph);
        clickItemEvent = new TransitionEvent<>(itemClick, false, true);
        // size变化时重新初始化item
        size.addOnCheckValueListener(size -> initItems());
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
            getGraph().createPath(new Click(child), clickedItem.getStaticExpectation(Timing.IMMEDIATELY, false, child)
                    .addFollowingExpectation(itemClick.getStaticExpectation(Timing.IMMEDIATELY, false, true)
                            .addFollowingExpectation(itemClick.getStaticExpectation(Timing.IMMEDIATELY, false, false))))
                    .addContextValue(child, true)
                    .addContextPredicate(size, new GreaterThan<>(index));
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

    public <V> V getValueFromClickedItem(Function<I, V> function) {
        I item = clickedItem.getCurrentValue();
        if (item != null) {
            return function.apply(item);
        }
        return null;
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

    public class ItemClick extends BooleanProperty {
        private ItemClick(Graph graph) {
            super(graph);
        }

        @EqualsPart
        @Visible
        public ListView<I> getListView() {
            return ListView.this;
        }

        @Override
        protected Boolean checkValue() {
            return false;
        }

        @Override
        protected ExternalEvent doSelfSwitch(Boolean to) {
            return null;
        }
    }
}
