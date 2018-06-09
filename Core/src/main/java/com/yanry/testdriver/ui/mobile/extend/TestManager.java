package com.yanry.testdriver.ui.mobile.extend;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.event.StateEvent;
import com.yanry.testdriver.ui.mobile.base.expectation.*;
import com.yanry.testdriver.ui.mobile.base.property.Property;
import com.yanry.testdriver.ui.mobile.base.property.SwitchBySearchProperty;
import com.yanry.testdriver.ui.mobile.extend.action.ClickOutside;
import com.yanry.testdriver.ui.mobile.extend.view.container.ViewContainer;
import lib.common.util.ReflectionUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.yanry.testdriver.ui.mobile.extend.TestManager.Visibility.*;

/**
 * Created by rongyu.yan on 4/17/2017.
 */
public class TestManager extends Graph {
    private LinkedList<Window> windowStack;
    private HashMap<Class<? extends Window>, Window> windowInstances;
    private HashMap<Class<? extends Property>, Property> propertyInstances;

    public TestManager(boolean debug) {
        super(debug);
        windowStack = new LinkedList<>();
        windowInstances = new HashMap<>();
        propertyInstances = new HashMap<>();
        Util.createPath(this, getProcessState().getStopProcessEvent(), new DynamicExpectation() {
            @Override
            protected boolean selfVerify() {
                windowStack.clear();
                return true;
            }
        });
    }

    public void registerWindows(Window... windows) {
        Stream.of(windows).filter(w -> windowInstances.put(w.getClass(), w) == null).collect(Collectors.toList()).forEach(w -> {
            ReflectionUtil.initStaticStringFields(w.getClass());
            w.addCases();
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
     *         <p>
     *         Jan 10, 2017
     */
    @Presentable
    public abstract class Window implements ViewContainer {
        private VisibilityState visibility;
        private ForegroundVerification foregroundVerification;
        private StateEvent<Visibility, VisibilityState> createEvent;
        private StateEvent<Visibility, VisibilityState> closeEvent;
        private StateEvent<Visibility, VisibilityState> resumeEvent;
        private StateEvent<Visibility, VisibilityState> pauseEvent;

        public Window() {
            visibility = new VisibilityState();
            foregroundVerification = new ForegroundVerification();
            createEvent = new StateEvent<>(visibility, NotCreated, Foreground);
            closeEvent = new StateEvent<>(visibility, Foreground, NotCreated);
            resumeEvent = new StateEvent<>(visibility, Background, Foreground);
            pauseEvent = new StateEvent<>(visibility, Foreground, Background);
        }

        protected abstract void addCases();

        public Path showOnStartUp(Timing timing) {
            return Util.createPath(TestManager.this, getProcessState().getStartProcessEvent(),
                    foregroundVerification.getExpectation(timing, true).addFollowingExpectation(new DynamicExpectation() {
                        @Override
                        protected boolean selfVerify() {
                            return verifySuperPaths(visibility, NotCreated, Foreground, () -> {
                                windowStack.add(Window.this);
                                return true;
                            });
                        }
                    }));
        }

        public Path popWindow(Window newWindow, Event inputEvent, Timing timing, boolean closeCurrent, boolean
                singleInstance) {
            return createPath(inputEvent, newWindow.foregroundVerification.getExpectation
                    (timing, true).addFollowingExpectation(new DynamicExpectation() {
                @Override
                protected boolean selfVerify() {
                    if (closeCurrent) {
                        verifySuperPaths(visibility, Foreground, NotCreated, () -> {
                            windowStack.removeLastOccurrence(this);
                            return true;
                        });
                    } else {
                        verifySuperPaths(visibility, Foreground, Background, () -> true);
                    }
                    if (singleInstance) {
                        windowStack.removeIf(w -> w == newWindow);
                    }
                    verifySuperPaths(newWindow.visibility, NotCreated, Foreground, () -> {
                        windowStack.add(newWindow);
                        return true;
                    });
                    return true;
                }
            }));
        }

        public Path close(Event inputEvent, Timing timing, Expectation... followingExpectations) {
            Expectation expectation = foregroundVerification.getExpectation(timing,
                    false).addFollowingExpectation(new DynamicExpectation() {
                @Override
                protected boolean selfVerify() {
                    verifySuperPaths(visibility, Foreground, NotCreated, () -> {
                        windowStack.removeLastOccurrence(this);
                        return true;
                    });
                    if (!windowStack.isEmpty()) {
                        verifySuperPaths(windowStack.getLast().visibility, Background, Foreground, () -> true);
                    }
                    return true;
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

        public Property<Visibility> getVisibility() {
            return visibility;
        }

        public StateEvent<Visibility, VisibilityState> getCreateEvent() {
            return createEvent;
        }

        public StateEvent<Visibility, VisibilityState> getCloseEvent() {
            return closeEvent;
        }

        public StateEvent<Visibility, VisibilityState> getResumeEvent() {
            return resumeEvent;
        }

        public StateEvent<Visibility, VisibilityState> getPauseEvent() {
            return pauseEvent;
        }

        public Graph getGraph() {
            return TestManager.this;
        }

        public <W extends Window> W getWindow(Class<W> clz) {
            return (W) windowInstances.get(clz);
        }

        public <V, P extends Property<V>> P getProperty(Class<P> clz) {
            return (P) propertyInstances.get(clz);
        }

        @Override
        public void present(Path path) {
            path.addInitState(visibility, Foreground);
        }

        public class VisibilityState extends SwitchBySelfProperty<Visibility> {

            @Override
            protected boolean dooSwitch(Visibility to) {
                Visibility currentValue = visibility.getCurrentValue();
                switch (to) {
                    case NotCreated:
                        return foregroundVerification.switchTo(false);
                    case Foreground:
                        if (currentValue == Background) {
                            Window top;
                            //downward
                            while ((top = windowStack.getLast()) != Window.this) {
                                if (!top.visibility.switchTo(NotCreated)) {
                                    break;
                                }
                            }
                            // upward
                            if (top != Window.this) {
                                return foregroundVerification.switchTo(true);
                            }
                            return true;
                        } else {
                            // not created
                            return foregroundVerification.switchTo(true);
                        }
                    case Background:
                        return findPathToRoll(p -> p.get(visibility) == Foreground, (p, v) ->
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
