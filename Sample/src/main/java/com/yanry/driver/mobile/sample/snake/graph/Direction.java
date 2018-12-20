package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;

import java.util.Set;
import java.util.stream.Stream;

public class Direction extends Property<DirectionValue> {
    public Direction(Graph graph) {
        super(graph);
    }

    @Override
    protected DirectionValue checkValue() {
        return DirectionValue.Up;
    }

    @Override
    protected ExternalEvent doSelfSwitch(DirectionValue to) {
        return null;
    }

    @Override
    protected Stream<DirectionValue> getValueStream(Set<DirectionValue> collectedValues) {
        return Stream.of(DirectionValue.values());
    }
}
