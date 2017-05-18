package com.yanry.testdriver.ui.mobile.extend.action;

import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.event.ActionEvent;
import com.yanry.testdriver.ui.mobile.base.property.Property;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class SwitchState<V> extends ActionEvent<Property<V>, V> {
    private V to;

    public SwitchState(Property<V> target, V to) {
        super(target);
        this.to = to;
    }

    @Presentable
    public V getTo() {
        return to;
    }
}
