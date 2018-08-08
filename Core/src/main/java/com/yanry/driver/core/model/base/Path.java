/**
 *
 */
package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.event.Event;
import com.yanry.driver.core.model.event.StateEvent;
import com.yanry.driver.core.model.runtime.Presentable;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
public class Path extends Case {
    private Event event;
    private Expectation expectation;

    public Path(Event event, Expectation expectation) {
        this.event = event;
        this.expectation = expectation;
    }

    @Override
    protected void execute(Graph graph) {
        graph.deepRoll(this);
    }

    @Override
    int getUnsatisfiedDegree(long timeFrame) {
        return getUnsatisfiedDegree(timeFrame, true);
    }

    int getUnsatisfiedDegree(long timeFrame, boolean isToRoll) {
        Property excludeProperty = null;
        boolean addOne = false;
        if (event instanceof StateEvent) {
            StateEvent stateEvent = (StateEvent) event;
            if (isToRoll) {
                Object from = stateEvent.getFrom();
                if (from != null && !from.equals(stateEvent.getProperty().getCurrentValue())) {
                    addOne = true;
                }
            } else {
                excludeProperty = stateEvent.getProperty();
            }
        }
        if (timeFrame == 0 || timeFrame != this.timeFrame) {
            Property finalExcludeProperty = excludeProperty;
            unsatisfiedDegree = getInitState().keySet().stream()
                    .filter(property -> !property.equals(finalExcludeProperty) && !getInitState().get(property).test(property.getCurrentValue()))
                    .mapToInt(prop -> 1).sum();
            this.timeFrame = timeFrame;
        }
        return unsatisfiedDegree + (addOne ? 1 : 0);
    }

    @Presentable
    public Event getEvent() {
        return event;
    }

    @Presentable
    public Expectation getExpectation() {
        return expectation;
    }
}
