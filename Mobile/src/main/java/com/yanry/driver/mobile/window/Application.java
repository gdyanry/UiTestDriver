package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.*;
import com.yanry.driver.core.model.event.SwitchStateAction;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.runtime.fetch.Select;
import com.yanry.driver.mobile.action.GlobalActions;
import com.yanry.driver.mobile.property.ProcessState;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by rongyu.yan on 4/17/2017.
 */
public class Application extends Property<Window> {
    private Map<Class<? extends Window>, Window> windowInstances;
    private ProcessState processState;

    public Application(StateSpace stateSpace) {
        super(stateSpace);
        windowInstances = new LinkedHashMap<>();
        processState = new ProcessState(stateSpace);
        // 初始状态
        processState.setInitValue(false);
        setInitValue(null);
        // 开启进程
        stateSpace.createPath(GlobalActions.clickLauncher(), processState.getStaticExpectation(Timing.IMMEDIATELY, false, true))
                .addContextValue(processState, false);
        // 退出进程
        stateSpace.createPath(new SwitchStateAction<>(processState, false),
                processState.getStaticExpectation(Timing.IMMEDIATELY, false, false))
                .addContextValue(processState, true);
        // 退出进程时清理当前窗口
        stateSpace.createPath(new TransitionEvent<>(processState, true, false),
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
            window.addCases(window.getStateSpace(), this);
        }
        addValue(windows);
    }

    public ProcessState getProcessState() {
        return processState;
    }

    @Override
    protected Window checkValue() {
        return getStateSpace().obtainValue(new Select<>(this));
    }

    @Override
    protected ExternalEvent doSelfSwitch(Window to, ActionFilter actionFilter) {
        return null;
    }

    @Override
    protected Stream<Window> getValueStream(Set<Window> collectedValues) {
        return collectedValues.stream();
    }
}
