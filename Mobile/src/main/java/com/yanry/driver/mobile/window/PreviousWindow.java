package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.CacheProperty;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.runtime.Presentable;

public class PreviousWindow extends CacheProperty<Window> {
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
