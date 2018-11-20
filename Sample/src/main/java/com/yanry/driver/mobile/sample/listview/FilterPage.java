package com.yanry.driver.mobile.sample.listview;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.mobile.window.Application;
import com.yanry.driver.mobile.window.Window;

public class FilterPage extends Window {
    public FilterPage(Graph graph, Application manager) {
        super(graph, manager);
    }

    @Override
    protected void addCases(Graph graph, Application manager) {
        closeOnPressBack();
    }
}
