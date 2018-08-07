package com.yanry.driver.mobile.sample.listview;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;

public class FilterPage extends Window {
    public FilterPage(WindowManager manager) {
        super(manager);
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        closeOnPressBack();
    }
}
