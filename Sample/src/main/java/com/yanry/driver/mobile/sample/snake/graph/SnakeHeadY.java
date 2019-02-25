package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.base.IntegerProperty;
import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.mobile.sample.snake.GameConfigure;

import java.util.Set;
import java.util.stream.Stream;

public class SnakeHeadY extends IntegerProperty {
    public SnakeHeadY(StateSpace stateSpace) {
        super(stateSpace);
    }

    @Override
    protected Integer checkValue(Integer expected) {
        return GameConfigure.ROW_COUNT / 2;
    }

    @Override
    protected Stream<Integer> getValueStream(Set<Integer> collectedValues) {
        return Stream.iterate(0, integer -> integer <= GameConfigure.ROW_COUNT, integer -> integer + 1);
    }
}
