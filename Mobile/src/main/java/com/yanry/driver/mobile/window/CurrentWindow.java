package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.CacheProperty;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.runtime.fetch.Select;

public class CurrentWindow extends CacheProperty<Window> {
    private WindowManager manager;

    CurrentWindow(Graph graph, WindowManager manager) {
        super(graph);
        this.manager = manager;
    }

    @Override
    protected Window checkValue() {
        Window[] options = new Window[manager.windowInstances.size() + 1];
        int i = 0;
        options[0] = manager.noWindow;
        for (Window window : manager.windowInstances.values()) {
            options[++i] = window;
        }
        return getGraph().obtainValue(new Select<>(this, options));
    }

    @Override
    protected SwitchResult doSelfSwitch(Window to) {
        return SwitchResult.NoAction;
    }
}
