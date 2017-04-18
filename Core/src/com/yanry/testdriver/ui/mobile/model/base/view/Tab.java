package com.yanry.testdriver.ui.mobile.model.base.view;

import com.yanry.testdriver.ui.mobile.model.base.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rongyu.yan on 1/17/2017.
 */
public class Tab extends View implements ViewContainer {
    private int index;
    private CurrentTab currentTab;

    public Tab(ViewContainer parent, String name, CurrentTab currentTab) {
        super(parent, name);
        currentTab.tabs.add(this);
        index = currentTab.tabs.indexOf(this);
        this.currentTab = currentTab;
    }

    public Path show(Event event) {
        Path path = new Path(currentTab.graph, getWindow(), event, new PermanentExpectation<>(currentTab, this, Timing.IMMEDIATELY));
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

    public static abstract class CurrentTab extends ObjectProperty<Tab> {
        private List<Tab> tabs;
        private Graph graph;

        public CurrentTab(Graph graph) {
            super(true);
            tabs = new ArrayList<>();
            this.graph = graph;
        }
    }
}
