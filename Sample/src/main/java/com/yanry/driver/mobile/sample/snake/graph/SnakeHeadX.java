package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.base.IntegerProperty;
import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.mobile.sample.snake.GameConfigure;

import java.util.Set;
import java.util.stream.Stream;

public class SnakeHeadX extends IntegerProperty {
    public SnakeHeadX(StateSpace stateSpace) {
        super(stateSpace);
    }

    @Override
    protected Integer checkValue(Integer expected) {
        return GameConfigure.COL_COUNT / 2;
    }

    @Override
    protected Stream<Integer> getValueStream(Set<Integer> collectedValues) {
        return Stream.iterate(0, i -> i <= GameConfigure.COL_COUNT, i -> i + 1);
    }
}
