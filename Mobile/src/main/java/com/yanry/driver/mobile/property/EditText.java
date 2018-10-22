package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.event.ExternalEvent;
import com.yanry.driver.mobile.action.EnterText;
import com.yanry.driver.mobile.view.View;

public class EditText extends Text {
    public EditText(View view) {
        super(view);
    }

    @Override
    protected ExternalEvent doSelfSwitch(String to) {
        ExternalEvent externalEvent = getView().switchToValue(true);
        if (externalEvent == null) {
            externalEvent = new EnterText(this, to);
        }
        return externalEvent;
    }
}
