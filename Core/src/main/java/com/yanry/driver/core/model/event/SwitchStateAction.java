package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.Presentable;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class SwitchStateAction<V> extends ActionEvent<Property<V>, V> {
    private V to;

    public SwitchStateAction(Property<V> target, V to) {
        super(target);
        this.to = to;
    }

    @Presentable
    public V getTo() {
        return to;
    }
}
