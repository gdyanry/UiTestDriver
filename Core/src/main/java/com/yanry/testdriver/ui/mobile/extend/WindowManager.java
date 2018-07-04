package com.yanry.testdriver.ui.mobile.extend;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.ProcessState;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.event.StateEvent;
import com.yanry.testdriver.ui.mobile.base.expectation.ActionExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.DDPropertyExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.property.Property;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.extend.action.ClickOutside;
import com.yanry.testdriver.ui.mobile.extend.view.container.ViewContainer;
import lib.common.util.ReflectionUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.yanry.testdriver.ui.mobile.extend.WindowManager.Visibility.*;

/**
 * Created by rongyu.yan on 4/17/2017.
 */
public class WindowManager extends Graph {
    private List<Window> windowInstances;
    private HashMap<Class<? extends Property>, Property> propertyInstances;
    private CurrentWindow currentWindow;
    private NoWindow noWindow;

    public WindowManager(boolean debug) {
        super(debug);
        windowInstances = new LinkedList<>();
        propertyInstances = new HashMap<>();
        currentWindow = new CurrentWindow(this);
        noWindow = new NoWindow();
        currentWindow.handleExpectation(noWindow, false);
        Util.createPath(this, new ProcessState(this).getStateEvent(true, false), currentWindow.getStaticExpectation(Timing.IMMEDIATELY, false, noWindow));
    }

    public void registerProperties(Property... properties) {
        for (Property property : properties) {
            propertyInstances.put(property.getClass(), property);
        }
    }

    public CurrentWindow getCurrentWindow() {
        return currentWindow;
    }

    public enum Visibility {
        NotCreated, Foreground, Background
    }

    /**
     * @author yanry
     * <p>
     * Jan 10, 2017
     */
    @Presentable
    public abstract class Window implements ViewContainer {
        private VisibilityState visibility;
        private StateEvent<Visibility> createEvent;
        private StateEvent<Visibility> closeEvent;
        private StateEvent<Visibility> resumeEvent;
        private StateEvent<Visibility> pauseEvent;
        private PreviousWindow previousWindow;

        public Window() {
            previousWindow = new PreviousWindow(WindowManager.this);
            previousWindow.handleExpectation(noWindow, false);
            visibility = new VisibilityState(WindowManager.this);
            visibility.handleExpectation(NotCreated, false);
            createEvent = new StateEvent<>(visibility, NotCreated, Foreground);
            closeEvent = new StateEvent<>(visibility, Foreground, NotCreated);
            resumeEvent = new StateEvent<>(visibility, Background, Foreground);
            pauseEvent = new StateEvent<>(visibility, Foreground, Background);
            if (!windowInstances.contains(this)) {
                windowInstances.add(this);
                ReflectionUtil.initStaticStringFields(getClass());
                addCases();
                if (!getClass().equals(NoWindow.class)) {
                    Util.createPath(WindowManager.this, new ProcessState(WindowManager.this).getStateEvent(true, false), visibility.getStaticExpectation(Timing.IMMEDIATELY, false, NotCreated));
                }
            }
        }

        protected abstract void addCases();

        public Path showOnStartUp(Timing timing) {
            return Util.createPath(WindowManager.this, new ProcessState(WindowManager.this).getStateEvent(false, true),
                    currentWindow.getStaticExpectation(timing, true, this)
                            .addFollowingExpectation(previousWindow.getStaticExpectation(timing, false, noWindow))
                            .addFollowingExpectation(visibility.getStaticExpectation(timing, false, Foreground)));
        }

        public Path popWindow(Window newWindow, Event inputEvent, Timing timing, boolean closeCurrent, boolean singleInstance) {
            return createPath(inputEvent, currentWindow.getStaticExpectation(timing, true, newWindow).addFollowingExpectation(new ActionExpectation() {
                @Override
                protected void run() {
                    if (singleInstance) {
                        handleSingleInstance(newWindow.previousWindow.getCurrentValue(), newWindow);
                    }
                }

                private void handleSingleInstance(Window queriedWindow, Window kickWindow) {
                    if (queriedWindow != null && !queriedWindow.equals(noWindow) && queriedWindow.previousWindow != null) {
                        Window previous = queriedWindow.previousWindow.getCurrentValue();
                        if (previous.equals(noWindow)) {
                            return;
                        } else if (previous.equals(kickWindow)) {
                            queriedWindow.previousWindow.handleExpectation(previous.previousWindow.getCurrentValue(), false);
                        } else {
                            handleSingleInstance(previous, kickWindow);
                        }
                    }
                }
            }).addFollowingExpectation(visibility.getStaticExpectation(timing, false, closeCurrent ? NotCreated : Background))
                    .addFollowingExpectation(newWindow.visibility.getStaticExpectation(timing, false, Foreground))
                    .addFollowingExpectation(newWindow.previousWindow.getDynamicExpectation(timing, false, () -> closeCurrent ? previousWindow.getCurrentValue() : this)));
        }

        public Path close(Event inputEvent, Timing timing, Expectation... followingExpectations) {
            Expectation expectation = currentWindow.getDynamicExpectation(timing, true, () -> previousWindow.getCurrentValue())
                    .addFollowingExpectation(visibility.getStaticExpectation(timing, false, NotCreated))
                    .addFollowingExpectation(new DDPropertyExpectation<>(Timing.IMMEDIATELY, false, () -> previousWindow.getCurrentValue().visibility, () -> Foreground));
            for (Expectation followingExpectation : followingExpectations) {
                expectation.addFollowingExpectation(followingExpectation);
            }
            return createPath(inputEvent, expectation);
        }

        public Path closeOnTouchOutside() {
            return close(new ClickOutside(this), Timing.IMMEDIATELY);
        }

        public Path createPath(Event event, Expectation expectation) {
            return Util.createPath(getManager(), event, expectation).addInitState(visibility, Foreground);
        }

        public Property<Visibility> getVisibility() {
            return visibility;
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

        public WindowManager getManager() {
            return WindowManager.this;
        }

        public <V, P extends Property<V>> P getProperty(Class<P> clz) {
            return (P) propertyInstances.get(clz);
        }

        @Override
        public void present(Path path) {
            path.addInitState(visibility, Foreground);
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && obj.getClass().equals(getClass());
        }

        private class PreviousWindow extends CacheProperty<Window> {
            public PreviousWindow(Graph graph) {
                super(graph);
            }

            private Window getWindow() {
                return Window.this;
            }

            @Override
            protected Window checkValue() {
                return noWindow;
            }

            @Override
            protected boolean doSelfSwitch(Window to) {
                return false;
            }

            @Override
            protected boolean equalsWithSameClass(Property<Window> property) {
                PreviousWindow previousWindow = (PreviousWindow) property;
                return getWindow().equals(previousWindow.getWindow());
            }
        }

        public class VisibilityState extends Property<Visibility> {

            public VisibilityState(Graph graph) {
                super(graph);
            }

            @Presentable
            public Window getWindow() {
                return Window.this;
            }

            @Override
            public void handleExpectation(Visibility expectedValue, boolean needCheck) {

            }

            @Override
            protected boolean selfSwitch(Visibility to) {
                return false;
            }

            @Override
            protected boolean equalsWithSameClass(Property<Visibility> property) {
                VisibilityState visibilityState = (VisibilityState) property;
                return getWindow().equals(visibilityState.getWindow());
            }

            @Override
            public Visibility getCurrentValue() {
                if (currentWindow.getCurrentValue().equals(Window.this)) {
                    return Foreground;
                } else if (checkExist(previousWindow)) {
                    return Background;
                } else {
                    return NotCreated;
                }
            }

            private boolean checkExist(PreviousWindow previousWindow) {
                Window window = previousWindow.getCurrentValue();
                if (window.equals(Window.this)) {
                    return true;
                } else if (window.equals(noWindow)) {
                    return false;
                } else {
                    return checkExist(window.previousWindow);
                }
            }
        }
    }

    public class CurrentWindow extends CacheProperty<Window> {

        public CurrentWindow(Graph graph) {
            super(graph);
        }

        @Override
        protected Window checkValue() {
            Window[] options = new Window[windowInstances.size()];
            int i = 0;
            for (Window window : windowInstances) {
                options[i++] = window;
            }
            return getGraph().checkState(new StateToCheck<>(this, options));
        }

        @Override
        protected boolean doSelfSwitch(Window to) {
            return false;
        }

        @Override
        protected boolean equalsWithSameClass(Property<Window> property) {
            return true;
        }
    }

    private class NoWindow extends Window {
        @Override
        protected void addCases() {

        }
    }
}
