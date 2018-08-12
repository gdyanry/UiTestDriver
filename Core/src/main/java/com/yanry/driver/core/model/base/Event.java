/**
 *
 */
package com.yanry.driver.core.model.base;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
public abstract class Event {
    private Object[] hashFields;

    protected abstract <V> boolean matches(Property<V> property, V fromValue, V toValue);

    protected abstract void addHashFields(ArrayList<Object> hashFields);

    protected abstract boolean equalsWithSameClass(Object object);

    @Override
    public final int hashCode() {
        if (hashFields == null) {
            ArrayList list = new ArrayList();
            addHashFields(list);
            hashFields = list.toArray();
        }
        return Objects.hash(hashFields);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !obj.getClass().equals(getClass())) {
            return false;
        }
        return equalsWithSameClass(obj);
    }
}
