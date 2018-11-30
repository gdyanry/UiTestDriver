package com.yanry.driver.core.model;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.State;
import com.yanry.driver.core.model.event.NegationEvent;
import com.yanry.driver.core.model.expectation.Timing;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

/**
 * @Author: yanry
 * @Date: 2018/10/31 23:10
 */
public class Divider extends BooleanProperty {
    private State[] states;

    public Divider(State... states) {
        super(states[0].getProperty().getGraph());
        this.states = states;
        for (State state : states) {
            // -> false
            Path toFalse = getGraph().createPath(new NegationEvent<>(state.getProperty(), state.getValuePredicate()),
                    getStaticExpectation(Timing.IMMEDIATELY, false, false));
            for (State s : states) {
                if (s != state) {
                    toFalse.addContextPredicate(s.getProperty(), s.getValuePredicate());
                }
            }
            // -> true
            Path toTrue = getGraph().createPath(new NegationEvent<>(state.getProperty(), state.getValuePredicate().not()),
                    getStaticExpectation(Timing.IMMEDIATELY, false, true));
            for (State s : states) {
                if (s != state) {
                    toTrue.addContextPredicate(s.getProperty(), s.getValuePredicate());
                }
            }
            // clean
            state.getProperty().addOnCleanListener(() -> clean());
        }
    }

    @Visible
    @EqualsPart
    public State[] getStates() {
        return states;
    }

    @Override
    protected Boolean checkValue() {
        for (State state : states) {
            if (!state.isSatisfied()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected ExternalEvent doSelfSwitch(Boolean to) {
        return null;
    }
}
