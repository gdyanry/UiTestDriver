package com.yanry.driver.core.model.property;

import com.yanry.driver.core.model.base.ActionGuard;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.State;
import com.yanry.driver.core.model.event.NegationEvent;
import com.yanry.driver.core.model.expectation.Timing;
import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.Visible;

/**
 * @Author: yanry
 * @Date: 2018/10/31 23:10
 */
public class Divider extends BooleanProperty {
    private String name;
    private State[] states;

    public Divider(String name, State... states) {
        super(states[0].getProperty().getStateSpace());
        this.name = name;
        this.states = states;
        for (State state : states) {
            Path toFalse = getStateSpace().createPath(new NegationEvent<>(state.getProperty(), state.getValuePredicate()),
                    getStaticExpectation(Timing.IMMEDIATELY, false, false));
            Path toTrue = getStateSpace().createPath(new NegationEvent<>(state.getProperty(), state.getValuePredicate().not()),
                    getStaticExpectation(Timing.IMMEDIATELY, false, true));
            for (State s : states) {
                if (s != state) {
                    toFalse.addContextPredicate(s.getProperty(), s.getValuePredicate());
                    toTrue.addContextPredicate(s.getProperty(), s.getValuePredicate());
                }
            }
            // cleanCache
            state.getProperty().addOnCleanListener(this::cleanCache);
            // check value
            state.getProperty().addOnValueUpdateListener(v -> refresh());
        }
    }

    @EqualsPart
    @Visible
    public String getName() {
        return name;
    }

    @EqualsPart
    public State[] getStates() {
        return states;
    }

    @Override
    protected Boolean checkValue(Boolean expected) {
        for (State state : states) {
            if (!state.isSatisfied()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected ExternalEvent doSelfSwitch(Boolean to, ActionGuard actionGuard) {
        return null;
    }
}
