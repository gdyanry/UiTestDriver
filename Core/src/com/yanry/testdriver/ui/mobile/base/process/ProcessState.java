package com.yanry.testdriver.ui.mobile.base.process;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.event.StateSwitchEvent;
import com.yanry.testdriver.ui.mobile.base.property.UnsearchableSwitchableProperty;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 3/9/2017.
 */
public class ProcessState extends UnsearchableSwitchableProperty<Boolean> {
    private Graph graph;
    private StateSwitchEvent<Boolean> startProcessEvent;
    private StateSwitchEvent<Boolean> stopProcessEvent;

    public ProcessState(Graph graph) {
        this.graph = graph;
        startProcessEvent = new StateSwitchEvent<>(this, false, true);
        stopProcessEvent = new StateSwitchEvent<>(this, true, false);
    }

    public StateSwitchEvent<Boolean> getStartProcessEvent() {
        return startProcessEvent;
    }

    public StateSwitchEvent<Boolean> getStopProcessEvent() {
        return stopProcessEvent;
    }

    @Override
    protected boolean doSwitch(Boolean to, List<Path> superPathContainer, Supplier<Boolean> finalCheck) {
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
