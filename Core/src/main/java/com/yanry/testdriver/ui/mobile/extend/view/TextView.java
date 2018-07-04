package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.property.Property;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.extend.view.container.ViewContainer;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;

import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 4/21/2017.
 */
public class TextView extends View {
    private TextValue text;

    public TextView(Graph graph, ViewContainer parent, ViewSelector selector) {
        super(graph, parent, selector);
        text = new TextValue(graph);
    }

    public TextValue getText() {
        return text;
    }

    @Presentable
    public class TextValue extends CacheProperty<String> {

        public TextValue(Graph graph) {
            super(graph);
        }

        @Presentable
        public TextView getTextView() {
            return TextView.this;
        }

        @Override
        protected String checkValue() {
            return getGraph().checkState(new StateToCheck<>(this));
        }

        @Override
        protected boolean doSelfSwitch(String to) {
            return false;
        }

        @Override
        protected boolean equalsWithSameClass(Property<String> property) {
            TextValue textValue = (TextValue) property;
            return TextView.this.equals(textValue.getTextView());
        }
    }
}
