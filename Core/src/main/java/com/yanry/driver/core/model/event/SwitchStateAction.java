package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.runtime.Presentable;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class SwitchStateAction<V> extends ActionEvent<SwitchStateAction<V>, Property<V>, V> {
    private V to;

    public SwitchStateAction(Property<V> target, V to) {
        super(a -> a.getTarget(), a -> a.to);
        setTarget(target);
        this.to = to;
    }

    public Path createPath() {
        return new Path(this, getTarget().getStaticExpectation(Timing.IMMEDIATELY, false, to));
    }

    @Presentable
    public V getTo() {
        return to;
    }
}
