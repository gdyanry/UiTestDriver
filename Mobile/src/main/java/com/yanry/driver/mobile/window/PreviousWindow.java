package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.event.ActionEvent;
import lib.common.util.object.Presentable;

public class PreviousWindow extends Property<Window> {
    private Window window;

    PreviousWindow(Graph graph, Window window) {
        super(graph);
        this.window = window;
    }

    @Presentable
    public Window getWindow() {
        return window;
    }

    @Override
    protected Window checkValue() {
        return window.getManager().noWindow;
    }

    @Override
    protected ActionEvent doSelfSwitch(Window to) {
        return null;
    }
}
