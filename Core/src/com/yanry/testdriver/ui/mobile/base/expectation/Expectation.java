package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.property.SearchableProperty;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * Created by rongyu.yan on 2/9/2017.
 */
public interface Expectation {
    boolean verifyBunch(List<Path> superPathContainer);

    boolean switchTest(BiPredicate<SearchableProperty, Object> predicate);

    boolean ifRecord();
}
