package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.*;
import com.yanry.driver.core.model.event.SwitchStateAction;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.runtime.fetch.Select;
import com.yanry.driver.mobile.action.ClickLauncher;
import com.yanry.driver.mobile.action.ViewAction;
import com.yanry.driver.mobile.property.ProcessState;
import com.yanry.driver.mobile.view.View;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by rongyu.yan on 4/17/2017.
 */
public class WindowManager extends Property<Window> implements Consumer<Path> {
    private Map<Class<? extends Window>, Window> windowInstances;
    private ProcessState processState;

    public WindowManager(Graph graph) {
        super(graph);
        graph.setPathAdjuster(this);
        windowInstances = new LinkedHashMap<>();
        processState = new ProcessState(graph);
        // 初始状态
        processState.setInitValue(false);
        setInitValue(null);
        // 开启进程
        graph.createPath(ClickLauncher.get(), processState.getStaticExpectation(Timing.IMMEDIATELY, false, true))
                .addContextState(processState, false);
        // 退出进程
        graph.createPath(new SwitchStateAction<>(processState, false),
                processState.getStaticExpectation(Timing.IMMEDIATELY, false, false))
                .addContextState(processState, true);
        // 退出进程时清理当前窗口
        graph.createPath(new TransitionEvent<>(processState, true, false),
                getStaticExpectation(Timing.IMMEDIATELY, false, null));
    }

    Window getWindowInstance(Class<? extends Window> type) {
        return windowInstances.get(type);
    }

    public void registerWindows(Window... windows) {
        if (windowInstances.size() > 0) {
            throw new IllegalAccessError("此方法只能调用一次");
        }
        for (Window window : windows) {
            windowInstances.put(window.getClass(), window);
        }
        for (Window window : windowInstances.values()) {
            window.addCases(window.getGraph(), this);
        }
        addValue(windows);
    }

    public ProcessState getProcessState() {
        return processState;
    }

    @Override
    protected Window checkValue() {
        return getGraph().obtainValue(new Select<>(this));
    }

    @Override
    protected ExternalEvent doSelfSwitch(Window to) {
        return null;
    }

    @Override
    protected Stream<Window> getValueStream(Set<Window> collectedValues) {
        return collectedValues.stream();
    }

    @Override
    public void accept(Path path) {
        Event event = path.getEvent();
        if (event instanceof ViewAction) {
            ViewAction viewAction = (ViewAction) event;
            path.addContextState(viewAction.getView(), true);
        } else if (event instanceof SwitchStateAction) {
            SwitchStateAction switchStateAction = (SwitchStateAction) event;
            Property property = switchStateAction.getProperty();
            if (property instanceof View) {
                View view = (View) property;
                path.addContextState(view, true);
            }
        }
    }
}
