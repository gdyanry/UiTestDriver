package com.yanry.testdriver.ui.mobile.base.process;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.event.StateEvent;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
public class ProcessState extends SwitchBySelfProperty<Boolean> {
    private Graph graph;
    private StateEvent<Boolean, ProcessState> startProcessEvent;
    private StateEvent<Boolean, ProcessState> stopProcessEvent;

    public ProcessState(Graph graph) {
        this.graph = graph;
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
    protected boolean dooSwitch(Boolean to) {
        if (to) {
            return getGraph().performAction(new StartProcess());
        }
        return getGraph().performAction(new StopProcess());
    }

    @Override
    protected Graph getGraph() {
        return graph;
    }

    @Override
    protected Boolean checkValue() {
        return false;
    }
}
