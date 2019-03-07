package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.base.ActionGuard;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.StateSpace;
import lib.common.util.ReflectionUtil;

import java.util.Set;
import java.util.stream.Stream;

public class Direction extends Property<String> {
    static {
        ReflectionUtil.initStaticStringFields(Direction.class);
    }

    public static String UP;
    public static String DOWN;
    public static String LEFT;
    public static String RIGHT;

    public Direction(StateSpace stateSpace) {
        super(stateSpace);
    }

    @Override
    protected String checkValue(String expected) {
        return UP;
    }

    @Override
    protected ExternalEvent doSelfSwitch(String to, ActionGuard actionGuard) {
        return null;
    }

    @Override
    protected Stream<String> getValueStream(Set<String> collectedValues) {
        return ReflectionUtil.getStaticStringFieldNames(getClass()).stream();
    }
}
