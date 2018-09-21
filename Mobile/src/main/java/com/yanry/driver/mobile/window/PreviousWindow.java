package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.Graph;
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
    protected SwitchResult doSelfSwitch(Window to) {
        return SwitchResult.NoAction;
    }
}
