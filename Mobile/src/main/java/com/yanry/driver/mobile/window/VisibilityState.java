package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.Set;
import java.util.stream.Stream;

public class VisibilityState extends Property<Visibility> {
    private Window window;

    VisibilityState(Graph graph, Window window) {
        super(graph);
        this.window = window;
    }

    @Visible
    @EqualsPart
    public Window getWindow() {
        return window;
    }

    @Override
    protected Visibility checkValue() {
        Window current = window.getManager().currentWindow.getCurrentValue();
        if (current.equals(window)) {
            return Visibility.Foreground;
        } else if (checkExist(current.previousWindow)) {
            return Visibility.Background;
        } else {
            return Visibility.NotCreated;
        }
    }

    @Override
    protected ExternalEvent doSelfSwitch(Visibility to) {
        return null;
    }

    @Override
    protected Stream<Visibility> getValueStream(Set<Visibility> collectedValues) {
        return Stream.of(Visibility.values());
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
