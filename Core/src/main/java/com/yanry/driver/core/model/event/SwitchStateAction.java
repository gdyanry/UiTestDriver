package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.runtime.Presentable;
import lib.common.model.EqualsProxy;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class SwitchStateAction<V> extends ActionEvent<Property<V>, V> {
    private V to;
    private EqualsProxy<SwitchStateAction<V>> equalsProxy;

    public SwitchStateAction(Property<V> target, V to) {
        super(target);
        this.to = to;
        equalsProxy = new EqualsProxy<>(this, a -> a.getTarget(), a -> a.to);
    }

    public Path createPath() {
        return new Path(this, getTarget().getStaticExpectation(Timing.IMMEDIATELY, false, to));
    }

    @Presentable
    public V getTo() {
        return to;
    }

    @Override
    public int hashCode() {
        return equalsProxy.getHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return equalsProxy.checkEquals(obj);
    }
}
