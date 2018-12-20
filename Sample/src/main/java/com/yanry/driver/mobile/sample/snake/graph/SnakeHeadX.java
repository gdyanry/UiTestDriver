package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.IntProperty;
import com.yanry.driver.mobile.sample.snake.GameConfigure;

import java.util.Set;
import java.util.stream.Stream;

public class SnakeHeadX extends IntProperty {
    public SnakeHeadX(Graph graph) {
        super(graph);
    }

    @Override
    protected Integer checkValue() {
        return GameConfigure.COL_COUNT / 2;
    }

    @Override
    protected Stream<Integer> getValueStream(Set<Integer> collectedValues) {
        return Stream.iterate(0, integer -> integer <= GameConfigure.COL_COUNT, integer -> integer + 1);
    }
}
