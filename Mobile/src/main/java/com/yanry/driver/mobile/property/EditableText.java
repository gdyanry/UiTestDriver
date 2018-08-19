package com.yanry.driver.mobile.property;

import com.yanry.driver.mobile.action.EnterText;
import com.yanry.driver.mobile.view.View;

public class EditableText extends Text {
    public EditableText(View view) {
        super(view);
    }

    @Override
    protected SwitchResult doSelfSwitch(String to) {
        if (getView().switchToValue(true)) {
            return SwitchResult.ActionNeedCheck;
        }
        if (getGraph().performAction(new EnterText(getView(), to))) {
            return SwitchResult.ActionNoCheck;
        }
        return SwitchResult.NoAction;
    }
}
