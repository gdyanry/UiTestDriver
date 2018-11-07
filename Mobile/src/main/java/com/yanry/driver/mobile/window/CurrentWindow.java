package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.fetch.Select;

import java.util.Set;
import java.util.stream.Stream;

public class CurrentWindow extends Property<Window> {

    CurrentWindow(Graph graph) {
        super(graph);
    }

    @Override
    protected Window checkValue() {
        return getGraph().obtainValue(new Select<>(this));
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
