package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.expectation.Timing;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class SwitchStateAction<V> extends ExpectationEvent {

    public SwitchStateAction(Property<V> property, V to) {
        super(property.getStaticExpectation(Timing.IMMEDIATELY, false, to));
    }
}
