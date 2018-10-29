package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ExpectationEvent;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.property.EditText;

/**
 * Created by rongyu.yan on 2/28/2017.
 */
public class EnterText extends ExpectationEvent {

    public EnterText(EditText editText, String text) {
        super(editText.getStaticExpectation(Timing.IMMEDIATELY, false, text));
    }
}
