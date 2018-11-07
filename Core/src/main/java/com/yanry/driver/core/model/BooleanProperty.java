package com.yanry.driver.core.model;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;

import java.util.Set;
import java.util.stream.Stream;

/**
 * @Author: yanry
 * @Date: 2018/11/7 0:01
 */
public abstract class BooleanProperty extends Property<Boolean> {
    public BooleanProperty(Graph graph) {
        super(graph);
    }

    @Override
    protected Stream<Boolean> getValueStream(Set<Boolean> collectedValues) {
        return Stream.of(false, true);
    }
}
