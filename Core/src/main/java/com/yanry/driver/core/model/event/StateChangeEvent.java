package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.ActionGuard;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.InternalEvent;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.predicate.Equals;

import java.util.Objects;

public class StateChangeEvent<V> extends InternalEvent<V> {
    public StateChangeEvent(Property<V> property) {
        super(property);
    }

    @Override
    protected boolean matches(V fromValue, V toValue) {
        return !Objects.equals(fromValue, toValue);
    }

    @Override
    protected ExternalEvent traverse(ActionGuard actionGuard) {
        return getProperty().switchTo(Equals.of(getProperty().getCurrentValue()).not(), actionGuard);
    }
}
