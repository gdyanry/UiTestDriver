package com.yanry.testdriver.ui.mobile.base.process;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.event.ValueSwitchEvent;
import com.yanry.testdriver.ui.mobile.base.property.UnsearchableSwitchableProperty;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
public class ProcessState extends UnsearchableSwitchableProperty<Boolean> {
    private Graph graph;
    private ValueSwitchEvent<Boolean> startProcessEvent;
    private ValueSwitchEvent<Boolean> stopProcessEvent;

    public ProcessState(Graph graph) {
        this.graph = graph;
        startProcessEvent = new ValueSwitchEvent<>(this, false, true);
        stopProcessEvent = new ValueSwitchEvent<>(this, true, false);
    }

    public ValueSwitchEvent<Boolean> getStartProcessEvent() {
        return startProcessEvent;
    }

    public ValueSwitchEvent<Boolean> getStopProcessEvent() {
        return stopProcessEvent;
    }

    @Override
    protected boolean doSwitch(Boolean to) {
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
