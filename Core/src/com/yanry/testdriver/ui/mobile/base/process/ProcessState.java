package com.yanry.testdriver.ui.mobile.base.process;

import com.yanry.testdriver.ui.mobile.base.*;
import com.yanry.testdriver.ui.mobile.base.event.StateTransitionEvent;
import com.yanry.testdriver.ui.mobile.base.StateProperty;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
public abstract class ProcessState extends StateProperty<Boolean> {
    private StateTransitionEvent<Boolean> startProcessEvent;
    private StateTransitionEvent<Boolean> stopProcessEvent;

    public ProcessState() {
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
    public boolean transitTo(Predicate<Boolean> to, List<Path> superPathContainer) {
        return getGraph().transitToState(this, to, superPathContainer);
    }

    @Override
    public Boolean checkValue() {
        return false;
    }

    @Override
    public boolean ifNeedVerification() {
        return false;
    }
}
