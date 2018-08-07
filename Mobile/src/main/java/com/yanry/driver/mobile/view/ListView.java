package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.base.Expectation;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.property.ListViewSize;
import com.yanry.driver.mobile.view.selector.ByIndex;
import com.yanry.driver.mobile.view.selector.ViewSelector;
import lib.common.model.Singletons;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 4/25/2017.
 */
public class ListView extends View {
    private ListViewSize size;

    public ListView(Graph graph, ViewContainer parent, ViewSelector selector) {
        super(graph, parent, selector);
        size = new ListViewSize(graph, this);
    }

    public void verifySize(int expectedSize, Function<Expectation, Path> verifySizePath) {
        verifySizePath.apply(size.getStaticExpectation(Timing.IMMEDIATELY, true, expectedSize));
    }

    public ListViewSize getSize() {
        return size;
    }

    public Supplier<View> getItemBySize(IntFunction<Integer> index) {
        return () -> {
            int iSize = size.getCurrentValue();
            if (iSize > 0) {
                return new ListViewItem(getGraph(), this, index.apply(iSize));
            }
            return null;
        };
    }

    public Supplier<View> getRandomItem() {
        return getItemBySize(size -> Singletons.get(Random.class).nextInt(size));
    }

    public Supplier<View> getItemByFilter(Predicate<View> filter) {
        return () -> {
            int iSize = size.getCurrentValue();
            for (int i = 0; i < iSize; i++) {
                ListViewItem item = new ListViewItem(getGraph(), this, i);
                if (filter.test(item)) {
                    return item;
                }
            }
            return null;
        };
    }
}
