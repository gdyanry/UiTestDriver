package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.mobile.action.EnterText;
import com.yanry.driver.mobile.view.View;

public class EditText extends Text {
    public EditText(View view) {
        super(view);
    }

    @Override
    protected ActionEvent doSelfSwitch(String to) {
        ActionEvent actionEvent = getView().switchToValue(true);
        if (actionEvent == null) {
            actionEvent = new EnterText(this, to);
        }
        return actionEvent;
    }
}
