package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.runtime.Presentable;

import java.util.ArrayList;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class SwitchStateAction<V> extends ActionEvent<SwitchStateAction<V>, Property<V>, V> {
    private V to;

    public SwitchStateAction(Property<V> target, V to) {
        super(target);
        this.to = to;
    }

    public Path createPath() {
        return new Path(this, getTarget().getStaticExpectation(Timing.IMMEDIATELY, false, to));
    }

    @Presentable
    public V getTo() {
        return to;
    }

    @Override
    protected void addHashFields(ArrayList<Object> hashFields) {
        super.addHashFields(hashFields);
        hashFields.add(to);
    }

    @Override
    protected boolean equalsWithSameClass(SwitchStateAction<V> vSwitchStateAction) {
        return super.equalsWithSameClass(vSwitchStateAction) && to.equals(vSwitchStateAction.to);
    }
}
