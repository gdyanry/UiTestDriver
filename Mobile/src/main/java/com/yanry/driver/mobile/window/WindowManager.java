package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.property.ProcessState;

import java.util.HashMap;

/**
 * Created by rongyu.yan on 4/17/2017.
 */
public class WindowManager {
    Graph graph;
    HashMap<Class<? extends Window>, Window> windowInstances;
    CurrentWindow currentWindow;
    private ProcessState processState;
    final NoWindow noWindow;

    public WindowManager(Graph graph) {
        this.graph = graph;
        windowInstances = new HashMap<>();
        currentWindow = new CurrentWindow(graph, this);
        processState = new ProcessState(graph);
        noWindow = new NoWindow(this);
        currentWindow.handleExpectation(noWindow, false);
        graph.addPath(new Path(processState.getStateEvent(true, false), currentWindow.getStaticExpectation(Timing.IMMEDIATELY, false, noWindow)));
    }

    public void addWindow(Window... windows) {
        if (windowInstances.size() > 0) {
            throw new IllegalAccessError("此方法只能调用一次");
        }
        for (Window window : windows) {
            windowInstances.put(window.getClass(), window);
        }
        for (Window window : windowInstances.values()) {
            window.addCases(graph, this);
        }
    }

    public ProcessState getProcessState() {
        return processState;
    }
}
