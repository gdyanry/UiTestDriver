package com.yanry.testdriver.ui.mobile.extend;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.SwitchPredicate;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.event.StateSwitchEvent;
import com.yanry.testdriver.ui.mobile.base.expectation.ActionExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.property.SearchableSwitchableProperty;
import com.yanry.testdriver.ui.mobile.base.property.SwitchableProperty;
import com.yanry.testdriver.ui.mobile.base.property.UnsearchableSwitchableProperty;
import com.yanry.testdriver.ui.mobile.extend.action.ClickOutside;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.container.ViewContainer;
import lib.common.util.ReflectionUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
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
            public void run() {
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
        private StateSwitchEvent<Visibility> createEvent;
        private StateSwitchEvent<Visibility> closeEvent;
        private StateSwitchEvent<Visibility> resumeEvent;
        private StateSwitchEvent<Visibility> pauseEvent;
        private HashMap<String, View> registeredViews;

        public Window() {
            visibility = new VisibilityState();
            foregroundVerification = new ForegroundVerification();
            createEvent = new StateSwitchEvent<>(visibility, NotCreated, Foreground);
            closeEvent = new StateSwitchEvent<>(visibility, Foreground, NotCreated);
            resumeEvent = new StateSwitchEvent<>(visibility, Background, Foreground);
            pauseEvent = new StateSwitchEvent<>(visibility, Foreground, Background);
            registeredViews = new HashMap<>();
        }

        protected abstract void addCases();

        public Path showOnStartUp(Timing timing) {
            return Util.createPath(TestManager.this, getProcessState().getStartProcessEvent(),
                    foregroundVerification.getStaticExpectation(timing, true)).addFollowingAction((superPaths) ->
                    verifySuperPaths(visibility, NotCreated, Foreground, superPaths, () -> {
                        windowStack.add(this);
                        return true;
                    }));
        }

        public Path popWindow(Window newWindow, Event inputEvent, Timing timing, boolean closeCurrent, boolean
                singleInstance) {
            return createPath(inputEvent, newWindow.foregroundVerification.getStaticExpectation
                    (timing, true)).addFollowingAction((superPaths) -> {
                if (closeCurrent) {
                    verifySuperPaths(visibility, Foreground, NotCreated, superPaths, () -> {
                        windowStack.removeLastOccurrence(this);
                        return true;
                    });
                } else {
                    verifySuperPaths(visibility, Foreground, Background, superPaths, () -> true);
                }
                if (singleInstance) {
                    windowStack.removeIf(w -> w == newWindow);
                }
                verifySuperPaths(newWindow.visibility, NotCreated, Foreground, superPaths, () -> {
                    windowStack.add(newWindow);
                    return true;
                });
            });
        }

        public Path close(Event inputEvent, Timing timing) {
            return createPath(inputEvent, foregroundVerification.getStaticExpectation(timing,
                    false)).addFollowingAction((superPaths) -> {
                verifySuperPaths(visibility, Foreground, NotCreated, superPaths, () -> {
                    windowStack.removeLastOccurrence(this);
                    return true;
                });
                if (!windowStack.isEmpty()) {
                    verifySuperPaths(windowStack.getLast().visibility, Background, Foreground,
                            superPaths, () -> true);
                }
            });
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

        public StateSwitchEvent<Visibility> getCreateEvent() {
            return createEvent;
        }

        public StateSwitchEvent<Visibility> getCloseEvent() {
            return closeEvent;
        }

        public StateSwitchEvent<Visibility> getResumeEvent() {
            return resumeEvent;
        }

        public StateSwitchEvent<Visibility> getPauseEvent() {
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

        @Override
        public void present(Path path) {
            path.addInitState(visibility, Foreground);
        }

        public class VisibilityState extends UnsearchableSwitchableProperty<Visibility> {

            @Override
            protected boolean doSwitch(Visibility to, List<Path> superPathContainer, Supplier<Boolean> finalCheck) {
                Visibility currentValue = visibility.getCurrentValue();
                switch (to) {
                    case NotCreated:
                        return switchToState(foregroundVerification, false, superPathContainer,
                                finalCheck);
                    case Foreground:
                        if (currentValue == Background) {
                            Window top;
                            //downward
                            while ((top = windowStack.getLast()) != Window.this) {
                                if (!top.visibility.switchTo(NotCreated, superPathContainer)) {
                                    break;
                                }
                            }
                            // upward
                            if (top != Window.this) {
                                return switchToState(foregroundVerification, true,
                                        superPathContainer, finalCheck);
                            }
                            return true;
                        } else {
                            // not created
                            return switchToState(foregroundVerification, true, superPathContainer,
                                    finalCheck);
                        }
                    case Background:
                        return findPathToRoll(superPathContainer, (SwitchPredicate<Boolean>) (path, property, toValue) ->
                                foregroundVerification != property && property.getClass() == ForegroundVerification.class &&
                                        toValue.equals(true) && path.get(visibility) == Foreground, finalCheck);
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

        public class ForegroundVerification extends SearchableSwitchableProperty<Boolean> {
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
            protected boolean ifNeedVerification() {
                return true;
            }
        }
    }
}
