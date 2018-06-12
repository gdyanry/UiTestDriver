package com.yanry.testdriver.ui.mobile.base.process;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.event.StateEvent;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
public class ProcessState extends CacheProperty<Boolean> {
    private StateEvent<Boolean, ProcessState> startProcessEvent;
    private StateEvent<Boolean, ProcessState> stopProcessEvent;

    public ProcessState() {
        startProcessEvent = new StateEvent<>(this, false, true);
        stopProcessEvent = new StateEvent<>(this, true, false);
    }

    public StateEvent<Boolean, ProcessState> getStartProcessEvent() {
        return startProcessEvent;
    }

    public StateEvent<Boolean, ProcessState> getStopProcessEvent() {
        return stopProcessEvent;
    }

    @Override
    protected Boolean checkValue(Graph graph) {
        return false;
    }

    @Override
    public boolean isCheckedByUser() {
        return false;
    }

    @Override
    protected boolean selfSwitch(Graph graph, Boolean to) {
        if (to) {
            return graph.performAction(new StartProcess());
        }
        return graph.performAction(new StopProcess());
    }
}
