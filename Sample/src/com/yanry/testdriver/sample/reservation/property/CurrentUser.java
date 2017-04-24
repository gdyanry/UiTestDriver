package com.yanry.testdriver.sample.reservation.property;

import com.yanry.testdriver.ui.mobile.extend.property.GeneralProperty;
import com.yanry.testdriver.ui.mobile.base.Graph;

/**
 * Created by rongyu.yan on 4/18/2017.
 */
public class CurrentUser<String> extends GeneralProperty {

    public CurrentUser(Graph graph, String... optionValues) {
        super(graph, false, optionValues);
    }
}
