package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.CacheProperty;
import com.yanry.driver.core.model.runtime.StateToCheck;

public class CurrentWindow extends CacheProperty<Window> {
    private WindowManager manager;

    CurrentWindow(Graph graph, WindowManager manager) {
        super(graph);
        this.manager = manager;
    }

    @Override
    protected Window checkValue() {
        Window[] options = new Window[manager.windowInstances.size()];
        int i = 0;
        for (Window window : manager.windowInstances) {
            options[i++] = window;
        }
        return getGraph().checkState(new StateToCheck<>(this, options));
    }

    @Override
    protected boolean doSelfSwitch(Window to) {
        return false;
    }
}
