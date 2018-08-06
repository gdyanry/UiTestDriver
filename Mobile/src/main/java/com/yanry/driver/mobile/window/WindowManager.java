package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.property.ProcessState;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by rongyu.yan on 4/17/2017.
 */
public class WindowManager {
    Graph graph;
    List<Window> windowInstances;
    CurrentWindow currentWindow;
    private ProcessState processState;
    final NoWindow noWindow;

    public WindowManager(Graph graph) {
        this.graph = graph;
        windowInstances = new LinkedList<>();
        currentWindow = new CurrentWindow(graph, this);
        processState = new ProcessState(graph);
        noWindow = new NoWindow(this);
        currentWindow.handleExpectation(noWindow, false);
        graph.addPath(new Path(processState.getStateEvent(true, false), currentWindow.getStaticExpectation(Timing.IMMEDIATELY, false, noWindow)));
    }

    /**
     * 所有window实例化完成后必须调用此方法添加路径到状态空间中。
     */
    public void setup() {
        windowInstances.forEach(window -> window.addCases(graph, this));
    }

    public ProcessState getProcessState() {
        return processState;
    }


}
