/**
 *
 */
package com.yanry.testdriver.ui.mobile.model.base.window;

import com.yanry.testdriver.ui.mobile.model.base.*;
import com.yanry.testdriver.ui.mobile.model.base.view.ViewContainer;

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

    public Path popOnStartUp(Timing timing) {
        return new Path(graph, null, graph.getProcessState().getStartProcessEvent(), new PermanentExpectation<>
                (foregroundVerification, true, timing)).addFollowingAction(() -> {
            graph.verifyPathsByTransitionEvent(createEvent, () -> {
                manager.add(this);
                return true;
            });
        });
    }

    public Path popWindow(Window newWindow, Event inputEvent, Timing timing, boolean closeCurrent, boolean
            singleInstance) {
        return new Path(graph, this, inputEvent, new PermanentExpectation<>(newWindow.foregroundVerification,
                true, timing)).addFollowingAction(() -> {
            if (closeCurrent) {
                graph.verifyPathsByTransitionEvent(closeEvent, () -> {
                    manager.removeLastOccurrence(this);
                    return true;
                });
            } else {
                graph.verifyPathsByTransitionEvent(pauseEvent, () -> true);
            }
            if (singleInstance) {
                manager.removeIf(w -> w == newWindow);
            }
            graph.verifyPathsByTransitionEvent(newWindow.createEvent, () -> {
                manager.add(newWindow);
                return true;
            });
        });
    }

    public Path close(Event inputEvent, Timing timing) {
        return new Path(graph, this, inputEvent, new PermanentExpectation<>(foregroundVerification, false,
                timing))
                .addFollowingAction(() -> {
                    graph.verifyPathsByTransitionEvent(closeEvent, () -> {
                        manager.removeLastOccurrence(this);
                        return true;
                    });
                    if (!manager.isEmpty()) {
                        graph.verifyPathsByTransitionEvent(manager.getLast().resumeEvent, () -> true);
                    }
                });
    }

    public ObjectProperty<Visibility> getState() {
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

    public class WindowState extends ObjectProperty<Visibility> {

        public WindowState() {
            super(true);
        }

        public Window getWindow() {
            return Window.this;
        }

        @Override
        public boolean transitTo(Graph graph, Visibility toValue, boolean isTransitEvent) {
            Visibility currentValue = state.getCurrentValue();
            if (currentValue == toValue) {
                return true;
            }
            switch (toValue) {
                case Background:
                    return graph.findPathToRoll(isTransitEvent, (p, pe) -> pe.getProperty() instanceof ForegroundVerification && pe.getValue().equals(true) && p.getWindow()
                            == state.getWindow());
                case Foreground:
                    if (currentValue == Visibility.Background) {
                        Window top;
                        //downward
                        while ((top = manager.getLast()) != Window.this) {
                            if (!top.state.transitTo(graph, Visibility.NotCreated, isTransitEvent)) {
                                break;
                            }
                        }
                        // upward
                        if (top != Window.this) {
                            return graph.findPathToRoll(isTransitEvent, (p, pe) -> pe.getProperty() ==
                                    foregroundVerification && pe.getValue().equals(true));
                        }
                        return true;
                    } else {
                        return graph.findPathToRoll(isTransitEvent, (p, pe) -> pe.getProperty() ==
                                foregroundVerification && pe.getValue().equals(true));
                    }
               default:
                    return graph.findPathToRoll(isTransitEvent, (p, pe) -> pe.getProperty() ==
                            foregroundVerification && pe.getValue().equals(false));
            }
        }

        @Override
        public Visibility checkValue(Timing timing) {
            return getCurrentValue();
        }

        @Override
        public Visibility getCurrentValue() {
            return manager.checkWindowVisibility(Window.this);
        }
    }

    public class ForegroundVerification extends GeneralProperty<Boolean> {

        public ForegroundVerification() {
            super(true, graph, false, true);
        }

        @Presentable
        public Window getWindow() {
            return Window.this;
        }
    }
}
