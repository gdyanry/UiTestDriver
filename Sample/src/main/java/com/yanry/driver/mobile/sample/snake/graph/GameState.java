package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;

import java.util.Set;
import java.util.stream.Stream;

public class GameState extends Property<GameStateValue> {
    public GameState(Graph graph) {
        super(graph);
    }

    @Override
    protected GameStateValue checkValue() {
        return GameStateValue.New;
    }

    @Override
    protected ExternalEvent doSelfSwitch(GameStateValue to) {
        return null;
    }

    @Override
    protected Stream<GameStateValue> getValueStream(Set<GameStateValue> collectedValues) {
        return Stream.of(GameStateValue.values());
    }
}
