/**
 *
 */
package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Event;
import com.yanry.driver.core.model.base.Property;

/**
 * @author yanry
 * <p>
 * Jan 6, 2017
 */
public class ActionEvent extends Event {

    @Override
    protected boolean matches(Property property, Object fromValue, Object toValue) {
        return false;
    }
}
