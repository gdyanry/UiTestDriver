/**
 *
 */
package com.yanry.driver.core.model.base;

import lib.common.util.object.ObjectUtil;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
public abstract class Event {
    protected abstract boolean matches(Property property, Object fromValue, Object toValue);

    @Override
    public final int hashCode() {
        return ObjectUtil.hashCode(this);
    }

    @Override
    public final boolean equals(Object obj) {
        return ObjectUtil.equals(this, obj);
    }
}
