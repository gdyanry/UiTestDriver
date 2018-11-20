package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.event.SwitchStateAction;
import com.yanry.driver.mobile.view.View;

public class EditText extends Text {
    public EditText(View view) {
        super(view);
    }

    @Override
    protected ExternalEvent doSelfSwitch(String to) {
        return new SwitchStateAction<>(this, to);
    }
}
