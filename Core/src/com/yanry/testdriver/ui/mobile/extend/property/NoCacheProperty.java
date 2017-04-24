package com.yanry.testdriver.ui.mobile.extend.property;

import com.yanry.testdriver.ui.mobile.base.StateProperty;
import lib.common.model.Singletons;

import java.util.Random;

/**
 * Created by rongyu.yan on 3/1/2017.
 */
public abstract class NoCacheProperty<V> extends StateProperty<V> {
    private int hashCode;

    public NoCacheProperty() {
        hashCode = Singletons.get(Random.class).nextInt();
    }

    @Override
    public V getCurrentValue() {
        return checkValue();
    }

    @Override
    public boolean ifNeedVerification() {
        return true;
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
