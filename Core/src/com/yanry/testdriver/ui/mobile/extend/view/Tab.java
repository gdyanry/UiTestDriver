package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.StateProperty;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by rongyu.yan on 1/17/2017.
 */
public class Tab extends View implements ViewContainer {
    private int index;
    private CurrentTab currentTab;
    private Graph graph;

    public Tab(Graph graph, ViewContainer parent, ViewSelector selector, CurrentTab currentTab) {
        super(parent, selector);
        currentTab.tabs.add(this);
        index = currentTab.tabs.indexOf(this);
        this.graph = graph;
        this.currentTab = currentTab;
    }

    public Path show(Event event) {
        Path path = Util.createPath(graph, getWindow(), event, currentTab.getExpectation(Timing
                .IMMEDIATELY, this));
        present(path);
        return path;
    }

    @Presentable
    public int getIndex() {
        return index;
    }

    public CurrentTab getCurrentTab() {
        return currentTab;
    }

    public void setIndex(int index) {
        Tab tab = currentTab.tabs.get(index);
        tab.index = this.index;
        this.index = index;
    }

    public static class CurrentTab extends StateProperty<Tab> {
        private List<Tab> tabs;

        public CurrentTab() {
            tabs = new ArrayList<>();
        }

        @Override
        public boolean transitTo(Predicate<Tab> to, List<Path> superPathContainer) {
            return getGraph().transitToState(this, to, superPathContainer);
        }

        @Override
        protected Tab checkValue() {
            return getGraph().checkState(new StateToCheck<>(this, tabs.toArray(new Tab[0])));
        }

        @Override
        protected Graph getGraph() {
            return tabs.get(0).graph;
        }

        @Override
        public boolean ifNeedVerification() {
            return true;
        }
    }
}
