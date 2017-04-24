package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;

import java.util.List;

/**
 * Created by rongyu.yan on 2/9/2017.
 */
public interface Expectation {
    boolean verify(List<Path> superPathContainer);

    boolean ifRecord();
}
