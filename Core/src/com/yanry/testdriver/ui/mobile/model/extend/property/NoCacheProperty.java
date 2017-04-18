package com.yanry.testdriver.ui.mobile.model.extend.property;

import com.yanry.testdriver.ui.mobile.model.base.ObjectProperty;
import com.yanry.testdriver.ui.mobile.model.base.Timing;
import lib.common.model.Singletons;

import java.util.Random;

/**
 * Created by rongyu.yan on 3/1/2017.
 */
public abstract class NoCacheProperty<V> extends ObjectProperty<V> {
    private int hashCode;

    public NoCacheProperty() {
        super(true);
        hashCode = Singletons.get(Random.class).nextInt();
    }

    @Override
    public V getCurrentValue() {
        return checkValue(Timing.IMMEDIATELY);
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
