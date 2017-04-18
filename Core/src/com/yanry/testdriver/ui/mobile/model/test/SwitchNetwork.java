package com.yanry.testdriver.ui.mobile.model.test;

import com.yanry.testdriver.ui.mobile.model.base.ActionEvent;
import com.yanry.testdriver.ui.mobile.model.base.Presentable;
import com.yanry.testdriver.ui.mobile.model.test.Network;

/**
 * Created by rongyu.yan on 3/3/2017.
 */
@Presentable
public class SwitchNetwork implements ActionEvent {
    private Network toState;

    public SwitchNetwork(Network toState) {
        this.toState = toState;
    }

    @Presentable
    public Network getToState() {
        return toState;
    }
}
