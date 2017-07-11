package com.yanry.testdriver.ui.mobile.extend;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.event.ValueSwitchEvent;
import com.yanry.testdriver.ui.mobile.base.expectation.AbstractExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.ActionExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.property.SwitchBySearchProperty;
import com.yanry.testdriver.ui.mobile.base.property.SwitchableProperty;
import com.yanry.testdriver.ui.mobile.base.property.SwitchBySelfProperty;
import com.yanry.testdriver.ui.mobile.extend.action.ClickOutside;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.container.ViewContainer;
import lib.common.util.ReflectionUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.yanry.testdriver.ui.mobile.extend.TestManager.Visibility.*;

/**
 * Created by rongyu.yan on 4/17/2017.
 */
public class TestManager extends Graph {
    private LinkedList<Window> windowStack;
    private HashMap<Class<? extends Window>, Window> windowInstances;
    private HashMap<Class<? extends SwitchableProperty>, SwitchableProperty> propertyInstances;

    public TestManager(boolean debug) {
        super(debug);
        windowStack = new LinkedList<>();
        windowInstances = new HashMap<>();
        propertyInstances = new HashMap<>();
        Util.createPath(this, getProcessState().getStopProcessEvent(), new ActionExpectation() {
            @Override
            protected void run(List<Path> superPathContainer) {
                windowStack.clear();
            }
        });
    }

    public void registerWindows(Window... windows) {
        Stream.of(windows).filter(w -> windowInstances.put(w.getClass(), w) == null).collect(Collectors.toList()).forEach(w -> {
            ReflectionUtil.initStaticStringFields(w.getClass());
            w.addCases();
        });
    }

    public void registerProperties(SwitchableProperty... properties) {
        for (SwitchableProperty property : properties) {
            propertyInstances.put(property.getClass(), property);
        }
    }

    public enum Visibility {
        NotCreated, Foreground, Background
    }

    /**
     * @author yanry
     *         <p>
     *         Jan 10, 2017
     */
    @Presentable
    public abstract class Window implements ViewContainer {
        private VisibilityState visibility;
        private ForegroundVerification foregroundVerification;
        private ValueSwitchEvent<Visibility> createEvent;
        private ValueSwitchEvent<Visibility> closeEvent;
        private ValueSwitchEvent<Visibility> resumeEvent;
        private ValueSwitchEvent<Visibility> pauseEvent;
        private HashMap<String, View> registeredViews;
        private HashMap<String, SwitchableProperty> registeredProps;

        public Window() {
            visibility = new VisibilityState();
            foregroundVerification = new ForegroundVerification();
            createEvent = new ValueSwitchEvent<>(visibility, NotCreated, Foreground);
            closeEvent = new ValueSwitchEvent<>(visibility, Foreground, NotCreated);
            resumeEvent = new ValueSwitchEvent<>(visibility, Background, Foreground);
            pauseEvent = new ValueSwitchEvent<>(visibility, Foreground, Background);
            registeredViews = new HashMap<>();
        }

        protected abstract void addCases();

        public Path showOnStartUp(Timing timing) {
            return Util.createPath(TestManager.this, getProcessState().getStartProcessEvent(),
                    foregroundVerification.getExpectation(timing, true).addFollowingExpectation(new ActionExpectation() {
                        @Override
                        protected void run(List<Path> superPathContainer) {
                            verifySuperPaths(visibility, NotCreated, Foreground, superPathContainer, () -> {
                                windowStack.add(Window.this);
                                return true;
                            });
                        }
                    }));
        }

        public Path popWindow(Window newWindow, Event inputEvent, Timing timing, boolean closeCurrent, boolean
                singleInstance) {
            return createPath(inputEvent, newWindow.foregroundVerification.getExpectation
                    (timing, true).addFollowingExpectation(new ActionExpectation() {
                @Override
                protected void run(List<Path> superPathContainer) {
                    if (closeCurrent) {
                        verifySuperPaths(visibility, Foreground, NotCreated, superPathContainer, () -> {
                            windowStack.removeLastOccurrence(this);
                            return true;
                        });
                    } else {
                        verifySuperPaths(visibility, Foreground, Background, superPathContainer, () -> true);
                    }
                    if (singleInstance) {
                        windowStack.removeIf(w -> w == newWindow);
                    }
                    verifySuperPaths(newWindow.visibility, NotCreated, Foreground, superPathContainer, () -> {
                        windowStack.add(newWindow);
                        return true;
                    });
                }
            }));
        }

        public Path close(Event inputEvent, Timing timing, Expectation... followingExpectations) {
            AbstractExpectation expectation = foregroundVerification.getExpectation(timing,
                    false).addFollowingExpectation(new ActionExpectation() {
                @Override
                protected void run(List<Path> superPathContainer) {
                    verifySuperPaths(visibility, Foreground, NotCreated, superPathContainer, () -> {
                        windowStack.removeLastOccurrence(this);
                        return true;
                    });
                    if (!windowStack.isEmpty()) {
                        verifySuperPaths(windowStack.getLast().visibility, Background, Foreground,
                                superPathContainer, () -> true);
                    }
                }
            });
            for (Expectation followingExpectation : followingExpectations) {
                expectation.addFollowingExpectation(followingExpectation);
            }
            return createPath(inputEvent, expectation);
        }

        public Path closeOnTouchOutside() {
            return close(new ClickOutside(this), Timing.IMMEDIATELY);
        }

        public Path createPath(Event event, Expectation expectation) {
            return Util.createPath(getGraph(), event, expectation).addInitState(visibility, Foreground);
        }

        public SwitchableProperty<Visibility> getVisibility() {
            return visibility;
        }

        public ValueSwitchEvent<Visibility> getCreateEvent() {
            return createEvent;
        }

        public ValueSwitchEvent<Visibility> getCloseEvent() {
            return closeEvent;
        }

        public ValueSwitchEvent<Visibility> getResumeEvent() {
            return resumeEvent;
        }

        public ValueSwitchEvent<Visibility> getPauseEvent() {
            return pauseEvent;
        }

        public Graph getGraph() {
            return TestManager.this;
        }

        public Window getWindow(Class<? extends Window> clz) {
            return windowInstances.get(clz);
        }

        public <V, P extends SwitchableProperty<V>> P getProperty(Class<P> clz) {
            return (P) propertyInstances.get(clz);
        }

        public void registerView(String tag, View view) {
            registeredViews.put(tag, view);
        }

        public <V extends View> V getView(String tag) {
            return (V) registeredViews.get(tag);
        }

        public <V> void registerProperty(String tag, SwitchableProperty<V> property) {
            registeredProps.put(tag, property);
        }

        public <V, P extends SwitchableProperty<V>> P getProperty(String tag) {
            return (P) registeredProps.get(tag);
        }

        @Override
        public void present(Path path) {
            path.addInitState(visibility, Foreground);
        }

        public class VisibilityState extends SwitchBySelfProperty<Visibility> {

            @Override
            protected boolean doSwitch(Visibility to) {
                Visibility currentValue = visibility.getCurrentValue();
                switch (to) {
                    case NotCreated:
                        return foregroundVerification.switchTo(false, null);
                    case Foreground:
                        if (currentValue == Background) {
                            Window top;
                            //downward
                            while ((top = windowStack.getLast()) != Window.this) {
                                if (!top.visibility.switchTo(NotCreated, null)) {
                                    break;
                                }
                            }
                            // upward
                            if (top != Window.this) {
                                return foregroundVerification.switchTo(true, null);
                            }
                            return true;
                        } else {
                            // not created
                            return foregroundVerification.switchTo(true, null);
                        }
                    case Background:
                        return findPathToRoll(null, p -> p.get(visibility) == Foreground, (p, v) ->
                                foregroundVerification != p && p.getClass() == ForegroundVerification.class &&
                                        v.equals(true));
                }
                return false;
            }

            @Override
            protected Graph getGraph() {
                return TestManager.this;
            }

            @Override
            protected Visibility checkValue() {
                return null;
            }

            @Override
            public Visibility getCurrentValue() {
                int index = windowStack.indexOf(Window.this);
                if (index == -1) {
                    return NotCreated;
                } else if (index == windowStack.size() - 1) {
                    return Foreground;
                } else {
                    return Background;
                }
            }
        }

        public class ForegroundVerification extends SwitchBySearchProperty<Boolean> {
            @Presentable
            public Window getWindow() {
                return Window.this;
            }

            @Override
            protected Boolean checkValue() {
                return null;
            }

            @Override
            public Boolean getCurrentValue() {
                return null;
            }

            @Override
            protected Graph getGraph() {
                return TestManager.this;
            }

            @Override
            protected boolean isVisibleToUser() {
                return true;
            }
        }
    }
}
