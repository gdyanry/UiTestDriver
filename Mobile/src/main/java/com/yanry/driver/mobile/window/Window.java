package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.*;
import com.yanry.driver.core.model.event.TransitionEvent;
import com.yanry.driver.core.model.expectation.ActionExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.state.Equals;
import com.yanry.driver.core.model.state.NotEquals;
import com.yanry.driver.mobile.action.ClickLauncher;
import com.yanry.driver.mobile.action.ClickOutside;
import com.yanry.driver.mobile.action.PressBack;
import com.yanry.driver.mobile.view.ViewContainer;
import lib.common.util.ReflectionUtil;

import java.util.stream.Stream;

public abstract class Window extends ViewContainer {
    private VisibilityState visibility;
    private TransitionEvent<Visibility> createEvent;
    private TransitionEvent<Visibility> closeEvent;
    private TransitionEvent<Visibility> resumeEvent;
    private TransitionEvent<Visibility> pauseEvent;
    PreviousWindow previousWindow;
    private WindowManager manager;

    public Window(Graph graph, WindowManager manager) {
        super(graph);
        this.manager = manager;
        previousWindow = new PreviousWindow(graph, this);
        previousWindow.handleExpectation(manager.noWindow, false);
        visibility = new VisibilityState(graph, this);
        visibility.handleExpectation(Visibility.NotCreated, false);
        createEvent = new TransitionEvent<>(visibility, Visibility.NotCreated, Visibility.Foreground);
        closeEvent = new TransitionEvent<>(visibility, Visibility.Foreground, Visibility.NotCreated);
        resumeEvent = new TransitionEvent<>(visibility, Visibility.Background, Visibility.Foreground);
        pauseEvent = new TransitionEvent<>(visibility, Visibility.Foreground, Visibility.Background);
        ReflectionUtil.initStaticStringFields(getClass());
        // visibility->Foreground
        graph.addPath(new Path(new TransitionEvent<>(manager.currentWindow, null, this),
                visibility.getStaticExpectation(Timing.IMMEDIATELY, false, Visibility.Foreground)));
        // 退出进程时visibility->NotCreated
        graph.addPath(new Path(new TransitionEvent<>(manager.getProcessState(), true, false),
                visibility.getStaticExpectation(Timing.IMMEDIATELY, false, this == manager.noWindow ? Visibility.Foreground : Visibility.NotCreated)));
        // 进入前台时visible->true
        createForegroundPath(new TransitionEvent<>(visibility, null, new Equals<>(Visibility.Foreground)),
                getStaticExpectation(Timing.IMMEDIATELY, false, true));
        // 退出前台时visible->false
        createForegroundPath(new TransitionEvent<>(visibility, null, new NotEquals(Visibility.Foreground) {
            @Override
            protected Stream getAllValues() {
                return Stream.of(Visibility.values());
            }
        }), getStaticExpectation(Timing.IMMEDIATELY, false, false));
    }

    public Path showOnLaunch(Timing timing) {
        Path path = new Path(ClickLauncher.get(), manager.currentWindow.getStaticExpectation(timing, true, this)
                .addFollowingExpectation(previousWindow.getStaticExpectation(Timing.IMMEDIATELY, false, manager.noWindow)))
                .addContextState(manager.currentWindow, manager.noWindow)
                .setBaseUnsatisfiedDegree(10000);
        getGraph().addPath(path);
        return path;
    }

    public Path popWindow(Class<? extends Window> windowType, Event inputEvent, Timing timing, boolean closeCurrent) {
        Window newWindow = getWindow(windowType);
        return createForegroundPath(inputEvent, manager.currentWindow.getStaticExpectation(timing, true, newWindow)
                .addFollowingExpectation(new ActionExpectation() {
                    @Override
                    protected void run() {
                        handleSingleInstance(newWindow.previousWindow.getCurrentValue(), newWindow);
                    }

                    private void handleSingleInstance(Window queriedWindow, Window kickWindow) {
                        if (queriedWindow != null && !queriedWindow.equals(manager.noWindow) && queriedWindow.previousWindow != null) {
                            Window previous = queriedWindow.previousWindow.getCurrentValue();
                            if (previous.equals(manager.noWindow)) {
                                return;
                            } else if (previous.equals(kickWindow)) {
                                queriedWindow.previousWindow.handleExpectation(previous.previousWindow.getCurrentValue(), false);
                            } else {
                                handleSingleInstance(previous, kickWindow);
                            }
                        }
                    }
                })
                // TODO 处理相同页面多个实例的情况
                .addFollowingExpectation(newWindow.previousWindow.getDynamicExpectation(Timing.IMMEDIATELY, false, () -> closeCurrent ? previousWindow.getCurrentValue() : this))
                // visibility->Background
                .addFollowingExpectation(visibility.getStaticExpectation(Timing.IMMEDIATELY, false, closeCurrent ? Visibility.NotCreated : Visibility.Background)));
    }

    public Path close(Event inputEvent, Timing timing) {
        return createForegroundPath(inputEvent, manager.currentWindow.getDynamicExpectation(timing, true, () -> previousWindow.getCurrentValue())
                .addFollowingExpectation(previousWindow.getStaticExpectation(Timing.IMMEDIATELY, false, manager.noWindow))
                .addFollowingExpectation(visibility.getStaticExpectation(Timing.IMMEDIATELY, false, Visibility.NotCreated)));
    }

    public Path closeOnPressBack() {
        return close(PressBack.get(), Timing.IMMEDIATELY);
    }

    public Path closeOnTouchOutside() {
        return close(new ClickOutside(this), Timing.IMMEDIATELY);
    }

    public Path createForegroundPath(Event event, Expectation expectation) {
        Path path = new Path(event, expectation);
        path.addContextState(visibility, Visibility.Foreground);
        getGraph().addPath(path);
        return path;
    }

    public <W extends Window> W getWindow(Class<W> windowCls) {
        return (W) manager.windowInstances.get(windowCls);
    }

    public WindowManager getManager() {
        return manager;
    }

    public VisibilityState getVisibility() {
        return visibility;
    }

    public PreviousWindow getPreviousWindow() {
        return previousWindow;
    }

    public TransitionEvent<Visibility> getCreateEvent() {
        return createEvent;
    }

    public TransitionEvent<Visibility> getCloseEvent() {
        return closeEvent;
    }

    public TransitionEvent<Visibility> getResumeEvent() {
        return resumeEvent;
    }

    public TransitionEvent<Visibility> getPauseEvent() {
        return pauseEvent;
    }

    protected abstract void addCases(Graph graph, WindowManager manager);

    @Override
    protected Boolean checkValue() {
        return visibility.getCurrentValue() == Visibility.Foreground;
    }

    @Override
    protected ExternalEvent doSelfSwitch(Boolean to) {
        return null;
    }
}
