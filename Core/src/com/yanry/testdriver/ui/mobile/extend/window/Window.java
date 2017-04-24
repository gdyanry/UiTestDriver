/**
 *
 */
package com.yanry.testdriver.ui.mobile.extend.window;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.*;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.event.StateTransitionEvent;
import com.yanry.testdriver.ui.mobile.base.StateProperty;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.action.ClickOutside;
import com.yanry.testdriver.ui.mobile.extend.property.GeneralProperty;
import com.yanry.testdriver.ui.mobile.extend.view.ViewContainer;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author yanry
 *         <p>
 *         Jan 10, 2017
 */
@Presentable
public class Window implements ViewContainer {
    private Object tag;
    private Graph graph;
    private WindowManager manager;
    private WindowState state;
    private ForegroundVerification foregroundVerification;
    private StateTransitionEvent<Visibility> createEvent;
    private StateTransitionEvent<Visibility> closeEvent;
    private StateTransitionEvent<Visibility> resumeEvent;
    private StateTransitionEvent<Visibility> pauseEvent;

    public Window(Object tag, Graph graph, WindowManager manager) {
        this.tag = tag;
        this.graph = graph;
        this.manager = manager;
        state = new WindowState();
        foregroundVerification = new ForegroundVerification();
        createEvent = new StateTransitionEvent<>(state, Visibility.Foreground, v -> v == Visibility.NotCreated);
        closeEvent = new StateTransitionEvent<>(state, Visibility.NotCreated, v -> v == Visibility.Foreground);
        resumeEvent = new StateTransitionEvent<>(state, Visibility.Foreground, v -> v == Visibility.Background);
        pauseEvent = new StateTransitionEvent<>(state, Visibility.Background, v -> v == Visibility.Foreground);
    }

    public Path showOnStartUp(Timing timing) {
        return Util.createPath(graph, null, graph.getProcessState().getStartProcessEvent(), foregroundVerification
                .getExpectation(timing, true)).addFollowingAction((sp) -> graph.verifySuperPaths
                (state, Visibility.NotCreated, Visibility.Foreground, sp,
                        () -> {
                            manager.add(this);
                            return true;
                        }));
    }

    public Path popWindow(Window newWindow, Event inputEvent, Timing timing, boolean closeCurrent, boolean
            singleInstance) {
        return Util.createPath(graph, this, inputEvent, newWindow.foregroundVerification.getExpectation(timing, true)).
                addFollowingAction((sp) -> {
                    if (closeCurrent) {
                        graph.verifySuperPaths(state, Visibility.Foreground, Visibility.NotCreated, sp, () -> {
                            manager.removeLastOccurrence(this);
                            return true;
                        });
                    } else {
                        graph.verifySuperPaths(state, Visibility.Foreground, Visibility.Background, sp, () -> true);
                    }
                    if (singleInstance) {
                        manager.removeIf(w -> w == newWindow);
                    }
                    graph.verifySuperPaths(newWindow.state, Visibility.NotCreated, Visibility.Foreground, sp, () -> {
                        manager.add(newWindow);
                        return true;
                    });
                });
    }

    public Path close(Event inputEvent, Timing timing) {
        return Util.createPath(graph, this, inputEvent, foregroundVerification.getExpectation(timing, false))
                .addFollowingAction((sp) -> {
                    graph.verifySuperPaths(state, Visibility.Foreground, Visibility.NotCreated, sp, () -> {
                        manager.removeLastOccurrence(this);
                        return true;
                    });
                    if (!manager.isEmpty()) {
                        graph.verifySuperPaths(manager.getLast().state, Visibility.Background, Visibility.Foreground,
                                sp, () -> true);
                    }
                });
    }

    public Path closeOnTouchOutside() {
        return close(new ClickOutside(this), Timing.IMMEDIATELY);
    }

    public StateProperty<Visibility> getState() {
        return state;
    }

    public ForegroundVerification getForegroundVerification() {
        return foregroundVerification;
    }

    public StateTransitionEvent<Visibility> getCreateEvent() {
        return createEvent;
    }

    public StateTransitionEvent<Visibility> getCloseEvent() {
        return closeEvent;
    }

    public StateTransitionEvent<Visibility> getResumeEvent() {
        return resumeEvent;
    }

    public StateTransitionEvent<Visibility> getPauseEvent() {
        return pauseEvent;
    }

    @Presentable
    public Object getTag() {
        return tag;
    }

    public class WindowState extends StateProperty<Visibility> {

        public Window getWindow() {
            return Window.this;
        }

        @Override
        public boolean transitTo(Predicate<Visibility> to, List<Path> superPathContainer) {
            Visibility currentValue = state.getCurrentValue();
            if (to.test(currentValue)) {
                return true;
            }
            if (to.test(Visibility.Background)) {
                return graph.findTransitionPathToRoll(superPathContainer, (p, pe) -> pe.getProperty() instanceof
                        ForegroundVerification &&
                        pe.getValue().equals(true) && p.get(state) == Visibility.Foreground);
            } else if (to.test(Visibility.Foreground)) {
                if (currentValue == Visibility.Background) {
                    Window top;
                    //downward
                    while ((top = manager.getLast()) != Window.this) {
                        if (!top.state.transitTo(v -> v == Visibility.NotCreated, null)) {
                            break;
                        }
                    }
                    // upward
                    if (top != Window.this) {
                        return graph.findTransitionPathToRoll(superPathContainer, (p, pe) -> pe.getProperty() ==
                                foregroundVerification && pe.getValue().equals(true));
                    }
                    return true;
                } else {
                    // not created
                    return graph.findTransitionPathToRoll(superPathContainer, (p, pe) -> pe.getProperty() ==
                            foregroundVerification && pe.getValue().equals(true));
                }
            } else if (to.test(Visibility.NotCreated)) {
                return graph.findTransitionPathToRoll(superPathContainer, (p, pe) -> pe.getProperty() ==
                        foregroundVerification && pe.getValue().equals(false));
            }
            return false;
        }

        @Override
        protected Graph getGraph() {
            return graph;
        }

        @Override
        public boolean ifNeedVerification() {
            return true;
        }

        @Override
        public Visibility checkValue() {
            return getCurrentValue();
        }

        @Override
        public Visibility getCurrentValue() {
            return manager.checkWindowVisibility(Window.this);
        }
    }

    public class ForegroundVerification extends GeneralProperty<Boolean> {

        public ForegroundVerification() {
            super(graph, true, false, true);
        }

        @Presentable
        public Window getWindow() {
            return Window.this;
        }

        @Override
        public Boolean getCurrentValue() {
            return null;
        }
    }
}
