package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.*;
import com.yanry.driver.core.model.event.NegationEvent;
import com.yanry.driver.core.model.expectation.ActionExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.mobile.action.ClickOutside;
import com.yanry.driver.mobile.action.GlobalActions;
import com.yanry.driver.mobile.view.ViewContainer;
import lib.common.util.ReflectionUtil;

public abstract class Window extends ViewContainer {
    private WindowState windowState;
    private TransitionEvent<String> createEvent;
    private TransitionEvent<String> closeEvent;
    private TransitionEvent<String> resumeEvent;
    private TransitionEvent<String> pauseEvent;
    private PreviousWindow previousWindow;
    private Application application;

    public Window(StateSpace stateSpace, Application application) {
        super(stateSpace);
        this.application = application;
        previousWindow = new PreviousWindow(stateSpace, this);
        windowState = new WindowState(stateSpace, this);
        windowState.setInitValue(WindowState.NOT_CREATED);
        createEvent = new TransitionEvent<>(windowState, WindowState.NOT_CREATED, WindowState.FOREGROUND);
        closeEvent = new NegationEvent<>(windowState, Equals.of(WindowState.NOT_CREATED).not());
        resumeEvent = new TransitionEvent<>(windowState, WindowState.BACKGROUND, WindowState.FOREGROUND);
        pauseEvent = new TransitionEvent<>(windowState, WindowState.FOREGROUND, WindowState.BACKGROUND);
        ReflectionUtil.initStaticStringFields(getClass());
        // windowState->FOREGROUND
        stateSpace.createPath(new NegationEvent<>(application, Equals.of(this).not()),
                windowState.getStaticExpectation(Timing.IMMEDIATELY, false, WindowState.FOREGROUND));
        // 退出进程时visibility->NOT_CREATED
        stateSpace.createPath(new TransitionEvent<>(application.getProcessState(), true, false),
                windowState.getStaticExpectation(Timing.IMMEDIATELY, false, WindowState.NOT_CREATED));

        Equals<String> foreground = Equals.of(WindowState.FOREGROUND);
        // 进入前台时visible->true
        getStateSpace().createPath(new NegationEvent<>(windowState, foreground.not()),
                getStaticExpectation(Timing.IMMEDIATELY, false, true));
        // 退出前台时visible->false
        getStateSpace().createPath(new NegationEvent<>(windowState, foreground),
                getStaticExpectation(Timing.IMMEDIATELY, false, false));
        // cleanCache
        getStateSpace().createPath(closeEvent, new ActionExpectation() {
            @Override
            protected void run() {
                cleanCache();
            }
        });
    }

    public Path showOnLaunch(Timing timing) {
        return getStateSpace().createPath(GlobalActions.clickLauncher(), application.getStaticExpectation(timing, true, this)
                .addFollowingExpectation(previousWindow.getStaticExpectation(Timing.IMMEDIATELY, false, null)))
                .addContextValue(application, null)
                .setBaseUnsatisfiedDegree(10000);
    }

    public Path popWindow(Class<? extends Window> windowType, Event inputEvent, Timing timing, boolean closeCurrent) {
        Window newWindow = getWindow(windowType);
        return getStateSpace().createPath(inputEvent, application.getStaticExpectation(timing, true, newWindow)
                .addFollowingExpectation(new ActionExpectation() {
                    @Override
                    protected void run() {
                        handleSingleInstance(newWindow.previousWindow.getCurrentValue(), newWindow);
                    }

                    private void handleSingleInstance(Window node, Window singleInstance) {
                        if (node != null) {
                            Window previous = node.previousWindow.getCurrentValue();
                            if (previous == null) {
                                return;
                            } else if (previous.equals(singleInstance)) {
                                node.previousWindow.handleExpectation(previous.previousWindow.getCurrentValue(), false);
                            } else {
                                handleSingleInstance(previous, singleInstance);
                            }
                        }
                    }
                })
                // TODO 处理相同页面多个实例的情况
                .addFollowingExpectation(newWindow.previousWindow.getDynamicExpectation(Timing.IMMEDIATELY, false,
                        () -> closeCurrent ? previousWindow.getCurrentValue() : Window.this))
                // windowState->BACKGROUND
                .addFollowingExpectation(windowState.getStaticExpectation(Timing.IMMEDIATELY, false, closeCurrent ? WindowState.NOT_CREATED : WindowState.BACKGROUND)))
                .addContextValue(this, true);
    }

    public Path close(Event inputEvent, Timing timing) {
        return getStateSpace().createPath(inputEvent, application.getDynamicExpectation(timing, true, () -> previousWindow.getCurrentValue())
                .addFollowingExpectation(previousWindow.getStaticExpectation(Timing.IMMEDIATELY, false, null))
                .addFollowingExpectation(windowState.getStaticExpectation(Timing.IMMEDIATELY, false, WindowState.NOT_CREATED)));
    }

    public Path closeOnPressBack() {
        return close(GlobalActions.pressBack(), Timing.IMMEDIATELY).addContextValue(this, true);
    }

    public Path closeOnTouchOutside() {
        return close(new ClickOutside(this), Timing.IMMEDIATELY);
    }

    public <W extends Window> W getWindow(Class<W> windowCls) {
        return (W) application.getWindowInstance(windowCls);
    }

    public Application getApplication() {
        return application;
    }

    public WindowState getWindowState() {
        return windowState;
    }

    public PreviousWindow getPreviousWindow() {
        return previousWindow;
    }

    public TransitionEvent<String> getCreateEvent() {
        return createEvent;
    }

    public TransitionEvent<String> getCloseEvent() {
        return closeEvent;
    }

    public TransitionEvent<String> getResumeEvent() {
        return resumeEvent;
    }

    public TransitionEvent<String> getPauseEvent() {
        return pauseEvent;
    }

    protected abstract void addCases(StateSpace stateSpace, Application manager);

    @Override
    protected Boolean checkValue(Boolean expected) {
        return windowState.getCurrentValue() == WindowState.FOREGROUND;
    }

    @Override
    protected ExternalEvent doSelfSwitch(Boolean to, ActionGuard actionGuard) {
        return null;
    }
}
