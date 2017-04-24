package com.yanry.testdriver.sample.debug;

import com.yanry.testdriver.ui.mobile.base.event.ActionEvent;
import com.yanry.testdriver.ui.mobile.base.Presentable;

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
