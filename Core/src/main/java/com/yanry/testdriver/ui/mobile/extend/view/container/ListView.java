package com.yanry.testdriver.ui.mobile.extend.view.container;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.property.Property;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByIndex;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;
import lib.common.model.Singletons;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 4/25/2017.
 */
public class ListView extends View implements ViewContainer {
    private ListViewSize size;

    public ListView(Graph graph, ViewContainer parent, ViewSelector selector) {
        super(graph, parent, selector);
        size = new ListViewSize(graph);
        refresh(getWindow().getCreateEvent());
    }

    public void refresh(Event event) {
        getWindow().createPath(event, size.getStaticExpectation(Timing.IMMEDIATELY, false, null));
    }

    public void verifySize(int expectedSize, Function<Expectation, Path> verifySizePath) {
        verifySizePath.apply(size.getStaticExpectation(Timing.IMMEDIATELY, true, expectedSize));
    }

    public Supplier<ListViewItem> getRandomItem() {
        return () -> {
            int iSize = size.getCurrentValue();
            if (iSize > 0) {
                return new ListViewItem(getGraph(), this, new ByIndex(Singletons.get(Random.class).nextInt(iSize)));
            }
            return null;
        };
    }

    public Supplier<ListViewItem> getItem(Predicate<ListViewItem> predicate) {
        return () -> {
            int iSize = size.getCurrentValue();
            for (int i = 0; i < iSize; i++) {
                ListViewItem item = new ListViewItem(getGraph(), this, new ByIndex(i));
                if (predicate.test(item)) {
                    return item;
                }
            }
            return null;
        };
    }

    @Override
    public void present(Path path) {
        getParent().present(path);
    }

    public class ListViewSize extends CacheProperty<Integer> {

        public ListViewSize(Graph graph) {
            super(graph);
        }

        @Presentable
        public ListView getListView() {
            return ListView.this;
        }

        @Override
        protected Integer checkValue() {
            return getGraph().checkState(new StateToCheck<>(this));
        }

        @Override
        protected boolean doSelfSwitch(Integer to) {
            return false;
        }

        @Override
        protected boolean equalsWithSameClass(Property<Integer> property) {
            ListViewSize size = (ListViewSize) property;
            return ListView.this.equals(size.getListView());
        }
    }
}
