package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.Graph;

public class NoWindow extends Window {
    public NoWindow(Graph graph, Application manager) {
        super(graph, manager);
    }

    @Override
    protected void addCases(Graph graph, Application manager) {

    }
}
