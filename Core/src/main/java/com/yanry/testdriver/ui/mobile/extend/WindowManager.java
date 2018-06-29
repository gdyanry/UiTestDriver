package com.yanry.testdriver.ui.mobile.extend;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.event.StateEvent;
import com.yanry.testdriver.ui.mobile.base.expectation.ActionExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.property.Property;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.extend.action.ClickOutside;
import com.yanry.testdriver.ui.mobile.extend.view.container.ViewContainer;
import lib.common.util.ReflectionUtil;

import java.util.HashMap;

import static com.yanry.testdriver.ui.mobile.extend.WindowManager.Visibility.*;

/**
 * Created by rongyu.yan on 4/17/2017.
 */
public class WindowManager extends Graph {
    private HashMap<Class<? extends Window>, Window> windowInstances;
    private HashMap<Class<? extends Property>, Property> propertyInstances;
    private CurrentWindow currentWindow;
    private NoWindow noWindow;

    public WindowManager(boolean debug) {
        super(debug);
        windowInstances = new HashMap<>();
        propertyInstances = new HashMap<>();
        currentWindow = new CurrentWindow();
        noWindow = new NoWindow();
        Util.createPath(this, getProcessState().getStateEvent(true, false), new ActionExpectation() {
            @Override
            protected void run() {
                currentWindow.setCacheValue(noWindow);
            }
        });
    }

    public void registerProperties(Property... properties) {
        for (Property property : properties) {
            propertyInstances.put(property.getClass(), property);
        }
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
            previousWindow = new PreviousWindow();
            visibility = new VisibilityState();
            createEvent = new StateEvent<>(visibility, NotCreated, Foreground);
            closeEvent = new StateEvent<>(visibility, Foreground, NotCreated);
            resumeEvent = new StateEvent<>(visibility, Background, Foreground);
            pauseEvent = new StateEvent<>(visibility, Foreground, Background);
            if (windowInstances.put(getClass(), this) == null) {
                ReflectionUtil.initStaticStringFields(getClass());
                addCases();
            }
        }

        protected abstract void addCases();

        public Path showOnStartUp(Timing timing) {
            return Util.createPath(WindowManager.this, getProcessState().getStateEvent(false, true),
                    currentWindow.getExpectation(timing, this).addFollowingExpectation(previousWindow.getExpectation(timing, noWindow))
                            .addFollowingExpectation(visibility.getExpectation(timing, Foreground)));
        }

        public Path popWindow(Window newWindow, Event inputEvent, Timing timing, boolean closeCurrent, boolean
                singleInstance) {
            return createPath(inputEvent, currentWindow.getExpectation(timing, newWindow).addFollowingExpectation(new ActionExpectation() {
                @Override
                protected void run() {
                    if (singleInstance) {
                        handleSingleInstance(newWindow.previousWindow.getCurrentValue(getManager()), newWindow);
                    }
                }

                private void handleSingleInstance(Window queriedWindow, Window targetWindow) {
                    if (queriedWindow != null && queriedWindow.previousWindow != null) {
                        Window previous = queriedWindow.previousWindow.getCurrentValue(getManager());
                        if (previous.equals(noWindow)) {
                            return;
                        } else if (previous.equals(targetWindow)) {
                            queriedWindow.previousWindow.setCacheValue(previous.previousWindow.getCurrentValue(getManager()));
                        } else {
                            handleSingleInstance(previous, targetWindow);
                        }
                    }
                }
            }).addFollowingExpectation(visibility.getExpectation(timing, closeCurrent ? NotCreated : Background))
                    .addFollowingExpectation(newWindow.visibility.getExpectation(timing, Foreground))
                    .addFollowingExpectation(newWindow.previousWindow.getExpectation(timing, closeCurrent ? previousWindow.getCurrentValue(getManager()) : this)));
        }

        public Path close(Event inputEvent, Timing timing, Expectation... followingExpectations) {
            Expectation expectation = currentWindow.getExpectation(timing,
                    previousWindow.getCurrentValue(getManager())).addFollowingExpectation(visibility.getExpectation(timing, NotCreated))
                    .addFollowingExpectation(previousWindow.getCurrentValue(getManager()).visibility.getExpectation(timing, Foreground));
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

            @Override
            protected Window checkValue(Graph graph) {
                return noWindow;
            }

            @Override
            protected boolean doSelfSwitch(Graph graph, Window to) {
                return false;
            }

            @Override
            public boolean isCheckedByUser() {
                return false;
            }
        }

        public class VisibilityState extends Property<Visibility> {

            @Presentable
            public Window getWindow() {
                return Window.this;
            }

            @Override
            protected boolean selfSwitch(Graph graph, Visibility to) {
                return false;
            }

            @Override
            public Visibility getCurrentValue(Graph graph) {
                if (currentWindow.getCurrentValue(graph).equals(Window.this)) {
                    return Foreground;
                } else if (exist(previousWindow, graph)) {
                    return Background;
                } else {
                    return NotCreated;
                }
            }

            private boolean exist(PreviousWindow previousWindow, Graph graph) {
                Window window = previousWindow.getCurrentValue(graph);
                if (window.equals(Window.this)) {
                    return true;
                } else if (window.equals(noWindow)) {
                    return false;
                } else {
                    return exist(window.previousWindow, graph);
                }
            }
        }
    }

    public class CurrentWindow extends CacheProperty<Window> {

        @Override
        protected Window checkValue(Graph graph) {
            Window[] options = new Window[windowInstances.size()];
            int i = 0;
            for (Window window : windowInstances.values()) {
                options[i++] = window;
            }
            return graph.checkState(new StateToCheck<>(this, options));
        }

        @Override
        protected boolean doSelfSwitch(Graph graph, Window to) {
            return false;
        }

        @Override
        public boolean isCheckedByUser() {
            return true;
        }
    }

    private class NoWindow extends Window {
        @Override
        protected void addCases() {

        }
    }
}
