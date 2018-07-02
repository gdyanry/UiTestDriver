/**
 *
 */
package com.yanry.testdriver.ui.mobile.base.event;

import com.yanry.testdriver.ui.mobile.base.property.Property;

/**
 * @author yanry
 *         <p>
 *         Jan 5, 2017
 */
public interface Event<V> {
    boolean matches(Property<V> property, V fromValue, V toValue);
}
