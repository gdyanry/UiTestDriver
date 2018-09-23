package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.event.ExpectationEvent;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.action.EnterText;
import com.yanry.driver.mobile.view.View;

public class EditableText extends Text {
    public EditableText(View view) {
        super(view);
    }

    @Override
    protected ActionEvent doSelfSwitch(String to) {
        ActionEvent actionEvent = getView().switchToValue(true);
        if (actionEvent == null) {
            actionEvent = new ExpectationEvent(new EnterText(getView(), to), getStaticExpectation(Timing.IMMEDIATELY, false, to));
        }
        return actionEvent;
    }
}
