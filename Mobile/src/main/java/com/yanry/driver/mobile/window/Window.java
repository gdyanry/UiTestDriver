package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.Expectation;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.event.Event;
import com.yanry.driver.core.model.event.StateEvent;
import com.yanry.driver.core.model.expectation.ActionExpectation;
import com.yanry.driver.core.model.expectation.DDPropertyExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.core.model.state.ValueEquals;
import com.yanry.driver.mobile.action.ClickLauncher;
import com.yanry.driver.mobile.action.ClickOutside;
import com.yanry.driver.mobile.action.PressBack;
import com.yanry.driver.mobile.view.ViewContainer;
import lib.common.util.ReflectionUtil;

@Presentable
public abstract class Window extends ViewContainer {
    private VisibilityState visibility;
    private StateEvent<Visibility> createEvent;
    private StateEvent<Visibility> closeEvent;
    private StateEvent<Visibility> resumeEvent;
    private StateEvent<Visibility> pauseEvent;
    PreviousWindow previousWindow;
    private WindowManager manager;

    public Window(Graph graph, WindowManager manager) {
        super(graph);
        this.manager = manager;
        previousWindow = new PreviousWindow(graph, this);
        previousWindow.handleExpectation(manager.noWindow, false);
        visibility = new VisibilityState(graph, this);
        visibility.handleExpectation(Visibility.NotCreated, false);
        createEvent = new StateEvent<>(visibility, Visibility.NotCreated, Visibility.Foreground);
        closeEvent = new StateEvent<>(visibility, Visibility.Foreground, Visibility.NotCreated);
        resumeEvent = new StateEvent<>(visibility, Visibility.Background, Visibility.Foreground);
        pauseEvent = new StateEvent<>(visibility, Visibility.Foreground, Visibility.Background);
        ReflectionUtil.initStaticStringFields(getClass());
        if (!getClass().equals(NoWindow.class)) {
            // 退出进程时visibility->NotCreated
            createPath(new StateEvent<>(manager.getProcessState(), true, false), visibility.getStaticExpectation(Timing.IMMEDIATELY, false, Visibility.NotCreated))
                    .addInitStatePredicate(visibility, new VisibilityNotEquals(Visibility.NotCreated));
            // 进入前台时visible->true
            createPath(new StateEvent<>(visibility, new VisibilityNotEquals(Visibility.Foreground), new ValueEquals<>(Visibility.Foreground)), getStaticExpectation(Timing.IMMEDIATELY, false, true));
            // 退出前台时visible->false
            createPath(new StateEvent<>(visibility, new ValueEquals<>(Visibility.Foreground), new VisibilityNotEquals(Visibility.Foreground)), getStaticExpectation(Timing.IMMEDIATELY, false, false));
        }
    }

    public Path showOnLaunch(Timing timing) {
        Path path = new Path(ClickLauncher.get(), manager.currentWindow.getStaticExpectation(timing, true, this)
                .addFollowingExpectation(previousWindow.getStaticExpectation(Timing.IMMEDIATELY, false, manager.noWindow))
                .addFollowingExpectation(visibility.getStaticExpectation(Timing.IMMEDIATELY, false, Visibility.Foreground)))
                .addInitState(manager.currentWindow, manager.noWindow);
        getGraph().addPath(path);
        return path;
    }

    public Path popWindow(Class<? extends Window> windowCls, Event inputEvent, Timing timing, boolean closeCurrent) {
        Window newWindow = getWindow(windowCls);
        return createPath(inputEvent, manager.currentWindow.getStaticExpectation(timing, true, newWindow)
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
                .addFollowingExpectation(visibility.getStaticExpectation(Timing.IMMEDIATELY, false, closeCurrent ? Visibility.NotCreated : Visibility.Background))
                .addFollowingExpectation(newWindow.visibility.getStaticExpectation(Timing.IMMEDIATELY, false, Visibility.Foreground)));
    }

    public Path close(Event inputEvent, Timing timing, Expectation... followingExpectations) {
        Expectation expectation = manager.currentWindow.getDynamicExpectation(timing, true, () -> previousWindow.getCurrentValue())
                .addFollowingExpectation(previousWindow.getStaticExpectation(Timing.IMMEDIATELY, false, manager.noWindow))
                .addFollowingExpectation(visibility.getStaticExpectation(Timing.IMMEDIATELY, false, Visibility.NotCreated))
                .addFollowingExpectation(new DDPropertyExpectation<>(Timing.IMMEDIATELY, false, () -> previousWindow.getCurrentValue().visibility, () -> Visibility.Foreground));
        for (Expectation followingExpectation : followingExpectations) {
            expectation.addFollowingExpectation(followingExpectation);
        }
        return createPath(inputEvent, expectation);
    }

    public Path closeOnPressBack() {
        return close(PressBack.get(), Timing.IMMEDIATELY);
    }

    public Path closeOnTouchOutside() {
        return close(new ClickOutside(this), Timing.IMMEDIATELY);
    }

    public Path createPath(Event event, Expectation expectation) {
        Path path = new Path(event, expectation);
        if (event instanceof StateEvent) {
            StateEvent stateEvent = (StateEvent) event;
            Property property = stateEvent.getProperty();
            if (!property.equals(manager.getProcessState())) {
                path.addInitState(manager.getProcessState(), true);
                if (!property.equals(visibility)) {
                    path.addInitState(visibility, Visibility.Foreground);
                }
            }
        } else {
            path.addInitState(visibility, Visibility.Foreground);
            path.addInitState(manager.getProcessState(), true);
        }
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

    public StateEvent<Visibility> getCreateEvent() {
        return createEvent;
    }

    public StateEvent<Visibility> getCloseEvent() {
        return closeEvent;
    }

    public StateEvent<Visibility> getResumeEvent() {
        return resumeEvent;
    }

    public StateEvent<Visibility> getPauseEvent() {
        return pauseEvent;
    }

    protected abstract void addCases(Graph graph, WindowManager manager);

    @Override
    public void handleExpectation(Boolean expectedValue, boolean needCheck) {

    }

    @Override
    public Boolean getCurrentValue() {
        return visibility.getCurrentValue() == Visibility.Foreground;
    }

    @Override
    protected boolean selfSwitch(Boolean to) {
        if (to) {
            return visibility.switchToValue(Visibility.Foreground);
        }
        return visibility.switchTo(new VisibilityNotEquals(Visibility.Foreground));
    }
}
