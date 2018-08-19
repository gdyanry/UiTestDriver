/**
 *
 */
package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Property;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
public interface Event {
    boolean matches(Property property, Object fromValue, Object toValue);
}
