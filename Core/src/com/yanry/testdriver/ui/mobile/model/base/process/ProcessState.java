package com.yanry.testdriver.ui.mobile.model.base.process;

import com.yanry.testdriver.ui.mobile.model.base.ObjectProperty;
import com.yanry.testdriver.ui.mobile.model.base.StateTransitionEvent;
import com.yanry.testdriver.ui.mobile.model.base.Timing;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
public class ProcessState extends ObjectProperty<Boolean> {
    private StateTransitionEvent<Boolean> startProcessEvent;
    private StateTransitionEvent<Boolean> stopProcessEvent;
    public ProcessState() {
        super(false);
        startProcessEvent = new StateTransitionEvent<>(this, true, v -> v == false);
        stopProcessEvent = new StateTransitionEvent<>(this, false, v -> v == true);
    }

    public StateTransitionEvent<Boolean> getStartProcessEvent() {
        return startProcessEvent;
    }

    public StateTransitionEvent<Boolean> getStopProcessEvent() {
        return stopProcessEvent;
    }

    @Override
    public Boolean checkValue(Timing timing) {
        return false;
    }
}
