package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.runtime.fetch.StringQuery;
import com.yanry.driver.mobile.view.View;

public class Text extends ViewProperty<String> {
    public Text(View view) {
        super(view);
    }

    @Override
    protected String doCheckValue() {
        return getGraph().obtainValue(new StringQuery(this));
    }

    @Override
    protected SwitchResult doSelfSwitch(String to) {
        return SwitchResult.NoAction;
    }
}
