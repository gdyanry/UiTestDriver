/**
 *
 */
package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.property.Property;

/**
 * @author yanry
 *         <p>
 *         Jan 5, 2017
 */
public interface Event<V> {
    boolean matches(Property<V> property, V fromValue, V toValue);
}
