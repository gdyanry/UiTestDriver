package com.yanry.testdriver.ui.mobile.model.test;

import com.yanry.testdriver.ui.mobile.model.base.GeneralProperty;
import com.yanry.testdriver.ui.mobile.model.base.Graph;

/**
 * Created by rongyu.yan on 3/8/2017.
 */
public class LoginState extends GeneralProperty<Boolean> {
    public LoginState(Graph graph) {
        super(false, graph, false, true);
    }
}
