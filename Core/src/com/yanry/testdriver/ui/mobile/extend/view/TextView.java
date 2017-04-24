package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.base.*;
import com.yanry.testdriver.ui.mobile.base.expectation.StatelessProperty;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;

/**
 * Created by rongyu.yan on 4/21/2017.
 */
public class TextView extends View {
    private TextValue text;

    public TextView(ViewContainer parent, ViewSelector selector) {
        super(parent, selector);
        text = new TextValue();
    }

    public TextValue getText() {
        return text;
    }

    @Presentable
    public class TextValue implements StatelessProperty<String> {

        @Presentable
        public TextView getView() {
            return TextView.this;
        }
    }
}
