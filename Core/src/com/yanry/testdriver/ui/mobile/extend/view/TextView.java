package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.SwitchBySearchProperty;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.extend.view.container.ViewContainer;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;

import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 4/21/2017.
 */
public class TextView extends View {
    private TextValue text;

    public TextView(ViewContainer parent, ViewSelector selector, Supplier<Boolean> defaultVisibility) {
        super(parent, selector, defaultVisibility);
        text = new TextValue();
    }

    public TextView(ViewContainer parent, ViewSelector selector) {
        this(parent, selector, null);
    }

    public TextValue getText() {
        return text;
    }

    @Presentable
    public class TextValue extends SwitchBySearchProperty<String> {

        @Override
        protected Graph getGraph() {
            return getWindow().getGraph();
        }

        @Override
        protected boolean isVisibleToUser() {
            return true;
        }

        @Override
        protected String checkValue() {
            return getGraph().checkState(new StateToCheck<>(this));
        }
    }
}
