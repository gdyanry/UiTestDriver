package com.yanry.testdriver.ui.mobile.base;

import com.yanry.testdriver.ui.mobile.base.property.SearchableSwitchableProperty;

/**
 * Created by rongyu.yan on 5/10/2017.
 */
public interface SwitchPredicate<V> {
    boolean test(Path path, SearchableSwitchableProperty<V> property, V toValue);
}
