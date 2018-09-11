/**
 *
 */
package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Property;
import lib.common.entity.HashAndEquals;

import java.util.function.Function;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
public abstract class Event<T extends Event<T>> extends HashAndEquals<T> {
    public Event(Function<T, Object>... concernedFields) {
        super(concernedFields);
    }

    public abstract boolean matches(Property property, Object fromValue, Object toValue);
}
