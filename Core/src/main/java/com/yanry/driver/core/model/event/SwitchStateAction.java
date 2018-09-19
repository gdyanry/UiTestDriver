package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.expectation.Timing;
import lib.common.util.object.HashAndEquals;
import lib.common.util.object.Presentable;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class SwitchStateAction<V> extends ActionEvent<Property<V>, V> {
    private V to;

    public SwitchStateAction(Property<V> target, V to) {
        super(target);
        this.to = to;
    }

    public Path createPath() {
        return new Path(this, getTarget().getStaticExpectation(Timing.IMMEDIATELY, false, to));
    }

    @HashAndEquals
    @Presentable
    public V getTo() {
        return to;
    }
}
