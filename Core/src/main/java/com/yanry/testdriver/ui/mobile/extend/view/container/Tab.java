package com.yanry.testdriver.ui.mobile.extend.view.container;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rongyu.yan on 1/17/2017.
 */
public class Tab extends View implements ViewContainer {
    private int index;
    private CurrentTab currentTab;

    public Tab(ViewContainer parent, ViewSelector selector, CurrentTab currentTab) {
        super(parent, selector);
        currentTab.tabs.add(this);
        index = currentTab.tabs.indexOf(this);
        this.currentTab = currentTab;
    }

    public Path show(Event event) {
        Path path = getWindow().createPath(event, currentTab.getStaticExpectation(Timing.IMMEDIATELY, true, this));
        present(path);
        return path;
    }

    @Presentable
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        Tab tab = currentTab.tabs.get(index);
        tab.index = this.index;
        this.index = index;
    }

    @Override
    public void present(Path path) {
        path.addInitState(currentTab, this);
        getParent().present(path);
    }

    public static class CurrentTab extends CacheProperty<Tab> {
        private Graph graph;
        private List<Tab> tabs;

        public CurrentTab(Graph graph) {
            this.graph = graph;
            tabs = new ArrayList<>();
        }

        @Override
        protected Tab checkValue(Graph graph) {
            return tabs.get(0);
        }

        @Override
        protected boolean doSelfSwitch(Graph graph, Tab to) {
            return false;
        }
    }
}
