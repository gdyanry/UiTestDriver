package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.mobile.window.Application;

public class ClickLauncher extends ExternalEvent {

    public ClickLauncher(Application application) {
        addPrecondition(application, Equals.of(null));
    }
}
