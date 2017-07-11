package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.property.SwitchBySearchProperty;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * Created by rongyu.yan on 2/9/2017.
 */
public interface Expectation {
    boolean verify(List<Path> superPathContainer);

    /**
     *
     * @param endStatePredicate
     * @return whether this expectation isSatisfied the given end state predicate.
     */
    boolean isSatisfied(BiPredicate<SwitchBySearchProperty, Object> endStatePredicate);

    boolean ifRecord();
}
