package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
import lib.common.util.object.Presentable;

public class VisibilityState extends Property<Visibility> {
    private Window window;

    VisibilityState(Graph graph, Window window) {
        super(graph);
        this.window = window;
    }

    @Presentable
    public Window getWindow() {
        return window;
    }

    @Override
    public void handleExpectation(Visibility expectedValue, boolean needCheck) {

    }

    @Override
    protected boolean selfSwitch(Visibility to) {
        return false;
    }

    @Override
    public Visibility getCurrentValue() {
        Window current = window.getManager().currentWindow.getCurrentValue();
        if (current.equals(window)) {
            return Visibility.Foreground;
        } else if (checkExist(current.previousWindow)) {
            return Visibility.Background;
        } else {
            return Visibility.NotCreated;
        }
    }

    private boolean checkExist(PreviousWindow previousWindow) {
        Window previous = previousWindow.getCurrentValue();
        if (window.equals(previous)) {
            return true;
        } else if (window.getManager().noWindow.equals(previous)) {
            return false;
        } else {
            return checkExist(previous.previousWindow);
        }
    }
}
