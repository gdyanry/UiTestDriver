package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.Set;
import java.util.stream.Stream;

public class PreviousWindow extends Property<Window> {
    private Window window;

    PreviousWindow(Graph graph, Window window) {
        super(graph);
        this.window = window;
    }

    @Visible
    @EqualsPart
    public Window getWindow() {
        return window;
    }

    @Override
    protected Window checkValue() {
        return null;
    }

    @Override
    protected ExternalEvent doSelfSwitch(Window to) {
        return null;
    }

    @Override
    protected Stream<Window> getValueStream(Set<Window> collectedValues) {
        return collectedValues.stream();
    }
}
