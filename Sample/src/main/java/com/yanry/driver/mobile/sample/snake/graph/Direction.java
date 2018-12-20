package com.yanry.driver.mobile.sample.snake.graph;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
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

    public Direction(Graph graph) {
        super(graph);
    }

    @Override
    protected String checkValue() {
        return UP;
    }

    @Override
    protected ExternalEvent doSelfSwitch(String to) {
        return null;
    }

    @Override
    protected Stream<String> getValueStream(Set<String> collectedValues) {
        return ReflectionUtil.getStaticStringFieldNames(getClass()).stream();
    }
}
