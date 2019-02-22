package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.base.ActionFilter;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.StateSpace;
import lib.common.util.ReflectionUtil;

import java.util.Set;
import java.util.stream.Stream;

public class GameState extends Property<String> {
    static {
        ReflectionUtil.initStaticStringFields(GameState.class);
    }

    public static String NEW;
    public static String MOVE;
    public static String PAUSE;
    public static String GAME_OVER;

    public GameState(StateSpace stateSpace) {
        super(stateSpace);
    }

    @Override
    protected String checkValue() {
        return NEW;
    }

    @Override
    protected ExternalEvent doSelfSwitch(String to, ActionFilter actionFilter) {
        return null;
    }

    @Override
    protected Stream<String> getValueStream(Set<String> collectedValues) {
        return ReflectionUtil.getStaticStringFieldNames(getClass()).stream();
    }
}
