package com.yanry.testdriver.sample.debug;

import com.yanry.testdriver.ui.mobile.extend.property.GeneralProperty;
import com.yanry.testdriver.ui.mobile.base.Graph;

/**
 * Created by rongyu.yan on 3/8/2017.
 */
public class LoginState extends GeneralProperty<Boolean> {
    public LoginState(Graph graph) {
        super(graph, false, false, true);
    }
}
