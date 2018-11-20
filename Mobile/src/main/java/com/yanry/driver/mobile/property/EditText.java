package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.event.SwitchStateAction;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.mobile.view.View;

public class EditText extends Text {
    public EditText(View view) {
        super(view);
    }

    @Override
    protected ExternalEvent doSelfSwitch(String to) {
        SwitchStateAction<String> action = new SwitchStateAction<>(this, to);
        action.addPrecondition(getView().getState(Equals.of(true)));
        return action;
    }
}
