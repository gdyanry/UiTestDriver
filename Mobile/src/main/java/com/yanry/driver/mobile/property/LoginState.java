package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.event.TransitionEvent;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.state.Equals;
import com.yanry.driver.core.model.state.Within;

/**
 * Created by rongyu.yan on 5/11/2017.
 */
public class LoginState extends Property<Boolean> {
    private CurrentUser currentUser;

    public LoginState(Graph graph, CurrentUser currentUser) {
        super(graph);
        this.currentUser = currentUser;
        Equals<String> isEmpty = new Equals<>("");
        Within<String> notEmpty = new Within<>(currentUser.getUserPasswordMap().keySet());
        // -> true
        graph.addPath(new Path(new TransitionEvent<>(currentUser, isEmpty, notEmpty),
                getStaticExpectation(Timing.IMMEDIATELY, false, true)));
        // -> false
        graph.addPath(new Path(new TransitionEvent<>(currentUser, notEmpty, isEmpty),
                getStaticExpectation(Timing.IMMEDIATELY, false, false)));
    }

    @Override
    protected Boolean checkValue() {
        return !currentUser.getCurrentValue().equals("");
    }

    @Override
    protected SwitchResult doSelfSwitch(Boolean to) {
        return null;
    }
}
