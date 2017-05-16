package com.yanry.testdriver.ui.mobile.extend.view.container;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.property.QueryableProperty;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByIndex;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;
import lib.common.model.Singletons;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 4/25/2017.
 */
public class ListView extends View implements ViewContainer {
    private ListViewSize size;

    public ListView(ViewContainer parent, ViewSelector selector) {
        super(parent, selector);
        size = new ListViewSize();
    }

    public ListViewSize getSize() {
        return size;
    }

    public void verifySize(Graph graph, int expectedSize, Function<Expectation, Path> verifySizePath) {
        String strSize = String.valueOf(expectedSize);
        verifySizePath.apply(size.getStaticExpectation(graph, Timing.IMMEDIATELY, strSize))
                .addFollowingAction((superPaths) -> size.setValue(strSize));
    }

    public Supplier<ListViewItem> getRandomItem(Graph graph) {
        return () -> {
            if (!size.hasValue()) {
                size.doQuery(graph);
            }
            int iSize = Integer.parseInt(size.getValue(true));
            if (iSize > 0) {
                return new ListViewItem(this, new ByIndex(Singletons.get(Random.class).nextInt(iSize)));
            }
            return null;
        };
    }

    @Override
    public void present(Path path) {
        getParent().present(path);
    }

    public class ListViewSize extends QueryableProperty {

        public ListViewSize() {
            super(ListView.this);
        }
    }
}
