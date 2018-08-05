package com.yanry.driver.mobile.property;

import com.yanry.driver.mobile.action.EnterText;
import com.yanry.driver.mobile.view.View;

public class EditableText extends Text {
    public EditableText(View view) {
        super(view);
    }

    @Override
    protected boolean doSelfSwitch(String to) {
        return getView().switchToVisible() || getGraph().performAction(new EnterText(getView(), to));
    }
}
