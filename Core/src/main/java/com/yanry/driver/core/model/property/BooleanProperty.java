package com.yanry.driver.core.model.property;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.StateSpace;

import java.util.Set;
import java.util.stream.Stream;

/**
 * @Author: yanry
 * @Date: 2018/11/7 0:01
 */
public abstract class BooleanProperty extends Property<Boolean> {
    public BooleanProperty(StateSpace stateSpace) {
        super(stateSpace);
    }

    @Override
    protected final Stream<Boolean> getValueStream(Set<Boolean> collectedValues) {
        return Stream.of(false, true);
    }
}
