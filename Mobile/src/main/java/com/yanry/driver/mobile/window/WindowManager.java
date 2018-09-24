package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.event.SwitchStateAction;
import com.yanry.driver.core.model.event.TransitionEvent;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.action.ClickLauncher;
import com.yanry.driver.mobile.property.ProcessState;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by rongyu.yan on 4/17/2017.
 */
public class WindowManager {
    Map<Class<? extends Window>, Window> windowInstances;
    CurrentWindow currentWindow;
    private ProcessState processState;
    final NoWindow noWindow;

    public WindowManager(Graph graph) {
        windowInstances = new LinkedHashMap<>();
        currentWindow = new CurrentWindow(graph, this);
        processState = new ProcessState(graph);
        // 开启进程
        graph.addPath(new Path(ClickLauncher.get(), processState.getStaticExpectation(Timing.IMMEDIATELY, false, true)).addContextState(processState, false));
        // 退出进程
        graph.addPath(new Path(new SwitchStateAction<>(processState, false), processState.getStaticExpectation(Timing.IMMEDIATELY, false, false))
                .addContextState(processState, true));
        noWindow = new NoWindow(graph, this);
        currentWindow.handleExpectation(noWindow, false);
        // 退出进程时清理当前窗口
        graph.addPath(new Path(new TransitionEvent<>(processState, true, false), currentWindow.getStaticExpectation(Timing.IMMEDIATELY, false, noWindow)));
    }

    public void addWindow(Window... windows) {
        if (windowInstances.size() > 0) {
            throw new IllegalAccessError("此方法只能调用一次");
        }
        for (Window window : windows) {
            windowInstances.put(window.getClass(), window);
        }
        for (Window window : windowInstances.values()) {
            window.addCases(window.getGraph(), this);
        }
    }

    public ProcessState getProcessState() {
        return processState;
    }
}
