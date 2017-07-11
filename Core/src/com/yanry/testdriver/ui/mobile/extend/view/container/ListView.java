package com.yanry.testdriver.ui.mobile.extend.view.container;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.expectation.ActionExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.property.QueryProperty;
import com.yanry.testdriver.ui.mobile.base.property.QueryableProperty;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByIndex;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;
import lib.common.model.Singletons;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 4/25/2017.
 */
public class ListView extends View implements ViewContainer {
    private ListViewSize size;

    public ListView(ViewContainer parent, ViewSelector selector) {
        super(parent, selector);
        size = new ListViewSize();
        refresh(getWindow().getCreateEvent());
    }

    public void refresh(Event event) {
        getWindow().createPath(event, new ActionExpectation() {
            @Override
            protected void run(List<Path> superPathContainer) {
                size.clearValue();
            }
        });
    }

    public void verifySize(int expectedSize, Function<Expectation, Path> verifySizePath) {
        String strSize = String.valueOf(expectedSize);
        verifySizePath.apply(size.getExpectation(Timing.IMMEDIATELY, strSize));
    }

    public Supplier<ListViewItem> getRandomItem() {
        return () -> {
            int iSize = Integer.parseInt(size.getCurrentValue());
            if (iSize > 0) {
                return new ListViewItem(this, new ByIndex(Singletons.get(Random.class).nextInt(iSize)));
            }
            return null;
        };
    }

    public Supplier<ListViewItem> getItem(Predicate<ListViewItem> predicate) {
        return () -> {
            int iSize = Integer.parseInt(size.getCurrentValue());
            for (int i = 0; i < iSize; i++) {
                ListViewItem item = new ListViewItem(this, new ByIndex(i));
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

    public class ListViewSize extends QueryProperty {

        @Override
        protected Graph getGraph() {
            return getWindow().getGraph();
        }

        @Override
        public Object getIdentifier() {
            return ListView.this;
        }
    }
}
