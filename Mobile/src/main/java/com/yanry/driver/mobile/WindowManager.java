package com.yanry.driver.mobile;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.Path;
import com.yanry.driver.core.model.event.Event;
import com.yanry.driver.core.model.event.StateEvent;
import com.yanry.driver.core.model.expectation.ActionExpectation;
import com.yanry.driver.core.model.expectation.DDPropertyExpectation;
import com.yanry.driver.core.model.expectation.Expectation;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.property.CacheProperty;
import com.yanry.driver.core.model.property.Property;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.core.model.runtime.StateToCheck;
import com.yanry.driver.mobile.action.ClickOutside;
import com.yanry.driver.mobile.property.ProcessState;
import com.yanry.driver.mobile.view.container.ViewContainer;
import lib.common.util.ReflectionUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by rongyu.yan on 4/17/2017.
 */
public class WindowManager {
    private Graph graph;
    private List<Window> windowInstances;
    private HashMap<Class<? extends Property>, Property> propertyInstances;
    private CurrentWindow currentWindow;
    public final NoWindow noWindow;

    private Path newPath(Event event, Expectation expectation) {
        Path path = new Path(event, expectation);
        if (!event.equals(new ProcessState(graph).getStateEvent(false, true))) {
            path.put(new ProcessState(graph), true);
        }
        graph.addPath(path);
        return path;
    }

    public WindowManager(Graph graph) {
        this.graph = graph;
        windowInstances = new LinkedList<>();
        propertyInstances = new HashMap<>();
        currentWindow = new CurrentWindow(graph);
        noWindow = new NoWindow();
        currentWindow.handleExpectation(noWindow, false);
        newPath(new ProcessState(graph).getStateEvent(true, false), currentWindow.getStaticExpectation(Timing.IMMEDIATELY, false, noWindow));
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
            previousWindow = new PreviousWindow(graph);
            previousWindow.handleExpectation(noWindow, false);
            visibility = new VisibilityState(graph);
            visibility.handleExpectation(WindowManager.Visibility.NotCreated, false);
            createEvent = new StateEvent<>(visibility, WindowManager.Visibility.NotCreated, WindowManager.Visibility.Foreground);
            closeEvent = new StateEvent<>(visibility, WindowManager.Visibility.Foreground, WindowManager.Visibility.NotCreated);
            resumeEvent = new StateEvent<>(visibility, WindowManager.Visibility.Background, WindowManager.Visibility.Foreground);
            pauseEvent = new StateEvent<>(visibility, WindowManager.Visibility.Foreground, WindowManager.Visibility.Background);
            if (!windowInstances.contains(this)) {
                windowInstances.add(this);
                ReflectionUtil.initStaticStringFields(getClass());
                addCases();
                if (!getClass().equals(NoWindow.class)) {
                    newPath(new ProcessState(graph).getStateEvent(true, false), visibility.getStaticExpectation(Timing.IMMEDIATELY, false, WindowManager.Visibility.NotCreated));
                }
            }
        }

        protected abstract void addCases();

        public Path showOnStartUp(Timing timing) {
            Path path = new Path(new ProcessState(graph).getStateEvent(false, true),
                    currentWindow.getStaticExpectation(timing, true, this)
                            .addFollowingExpectation(previousWindow.getStaticExpectation(Timing.IMMEDIATELY, false, noWindow))
                            .addFollowingExpectation(visibility.getStaticExpectation(Timing.IMMEDIATELY, false, WindowManager.Visibility.Foreground)));
            graph.addPath(path);
            return path;
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
            }).addFollowingExpectation(newWindow.previousWindow.getDynamicExpectation(Timing.IMMEDIATELY, false, () -> closeCurrent ? previousWindow.getCurrentValue() : this))
                    .addFollowingExpectation(visibility.getStaticExpectation(Timing.IMMEDIATELY, false, closeCurrent ? WindowManager.Visibility.NotCreated : WindowManager.Visibility.Background))
                    .addFollowingExpectation(newWindow.visibility.getStaticExpectation(Timing.IMMEDIATELY, false, WindowManager.Visibility.Foreground)));
        }

        public Path close(Event inputEvent, Timing timing, Expectation... followingExpectations) {
            Expectation expectation = currentWindow.getDynamicExpectation(timing, true, () -> previousWindow.getCurrentValue())
                    .addFollowingExpectation(previousWindow.getStaticExpectation(Timing.IMMEDIATELY, false, noWindow))
                    .addFollowingExpectation(visibility.getStaticExpectation(Timing.IMMEDIATELY, false, WindowManager.Visibility.NotCreated))
                    .addFollowingExpectation(new DDPropertyExpectation<>(Timing.IMMEDIATELY, false, () -> previousWindow.getCurrentValue().visibility, () -> WindowManager.Visibility.Foreground));
            for (Expectation followingExpectation : followingExpectations) {
                expectation.addFollowingExpectation(followingExpectation);
            }
            return createPath(inputEvent, expectation);
        }

        public Path closeOnTouchOutside() {
            return close(new ClickOutside(this), Timing.IMMEDIATELY);
        }

        public Path createPath(Event event, Expectation expectation) {
            return newPath(event, expectation).addInitState(visibility, WindowManager.Visibility.Foreground);
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

        public WindowManager getManager() {
            return WindowManager.this;
        }

        public <V, P extends Property<V>> P getProperty(Class<P> clz) {
            return (P) propertyInstances.get(clz);
        }

        @Override
        public void present(Path path) {
            path.addInitState(visibility, WindowManager.Visibility.Foreground);
        }

        @Override
        public final boolean equals(Object obj) {
            if (obj != null && obj.getClass().equals(getClass())) {
                Window window = (Window) obj;
                return window.getManager().equals(getManager());
            }
            return false;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(WindowManager.this, getClass());
        }

        public class PreviousWindow extends CacheProperty<Window> {
            public PreviousWindow(Graph graph) {
                super(graph);
            }

            @Presentable
            public Window getWindow() {
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
                Window current = currentWindow.getCurrentValue();
                if (current.equals(Window.this)) {
                    return WindowManager.Visibility.Foreground;
                } else if (checkExist(current.previousWindow)) {
                    return WindowManager.Visibility.Background;
                } else {
                    return WindowManager.Visibility.NotCreated;
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
