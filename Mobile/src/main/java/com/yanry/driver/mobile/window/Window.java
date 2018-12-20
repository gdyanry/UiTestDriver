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
    private VisibilityState visibility;
    private TransitionEvent<Visibility> createEvent;
    private TransitionEvent<Visibility> closeEvent;
    private TransitionEvent<Visibility> resumeEvent;
    private TransitionEvent<Visibility> pauseEvent;
    private PreviousWindow previousWindow;
    private Application application;

    public Window(Graph graph, Application application) {
        super(graph);
        this.application = application;
        previousWindow = new PreviousWindow(graph, this);
        visibility = new VisibilityState(graph, this);
        visibility.setInitValue(Visibility.NotCreated);
        createEvent = new TransitionEvent<>(visibility, Visibility.NotCreated, Visibility.Foreground);
        closeEvent = new NegationEvent<>(visibility, Equals.of(Visibility.NotCreated).not());
        resumeEvent = new TransitionEvent<>(visibility, Visibility.Background, Visibility.Foreground);
        pauseEvent = new TransitionEvent<>(visibility, Visibility.Foreground, Visibility.Background);
        ReflectionUtil.initStaticStringFields(getClass());
        // visibility->Foreground
        graph.createPath(new NegationEvent<>(application, Equals.of(this).not()),
                visibility.getStaticExpectation(Timing.IMMEDIATELY, false, Visibility.Foreground));
        // 退出进程时visibility->NotCreated
        graph.createPath(new TransitionEvent<>(application.getProcessState(), true, false),
                visibility.getStaticExpectation(Timing.IMMEDIATELY, false, Visibility.NotCreated));

        Equals<Visibility> foreground = Equals.of(Visibility.Foreground);
        // 进入前台时visible->true
        getGraph().createPath(new NegationEvent<>(visibility, foreground.not()),
                getStaticExpectation(Timing.IMMEDIATELY, false, true));
        // 退出前台时visible->false
        getGraph().createPath(new NegationEvent<>(visibility, foreground),
                getStaticExpectation(Timing.IMMEDIATELY, false, false));
        // cleanCache
        getGraph().createPath(closeEvent, new ActionExpectation() {
            @Override
            protected void run() {
                cleanCache();
            }
        });
    }

    public Path showOnLaunch(Timing timing) {
        return getGraph().createPath(GlobalActions.clickLauncher(), application.getStaticExpectation(timing, true, this)
                .addFollowingExpectation(previousWindow.getStaticExpectation(Timing.IMMEDIATELY, false, null)))
                .setBaseUnsatisfiedDegree(10000);
    }

    public Path popWindow(Class<? extends Window> windowType, Event inputEvent, Timing timing, boolean closeCurrent) {
        Window newWindow = getWindow(windowType);
        return getGraph().createPath(inputEvent, application.getStaticExpectation(timing, true, newWindow)
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
                // visibility->Background
                .addFollowingExpectation(visibility.getStaticExpectation(Timing.IMMEDIATELY, false, closeCurrent ? Visibility.NotCreated : Visibility.Background)))
                .addContextValue(this, true);
    }

    public Path close(Event inputEvent, Timing timing) {
        return getGraph().createPath(inputEvent, application.getDynamicExpectation(timing, true, () -> previousWindow.getCurrentValue())
                .addFollowingExpectation(previousWindow.getStaticExpectation(Timing.IMMEDIATELY, false, null))
                .addFollowingExpectation(visibility.getStaticExpectation(Timing.IMMEDIATELY, false, Visibility.NotCreated)));
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

    protected abstract void addCases(Graph graph, Application manager);

    @Override
    protected Boolean checkValue() {
        return visibility.getCurrentValue() == Visibility.Foreground;
    }

    @Override
    protected ExternalEvent doSelfSwitch(Boolean to) {
        return null;
    }
}
