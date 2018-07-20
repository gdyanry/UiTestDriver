package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.property.CacheProperty;
import com.yanry.driver.core.model.property.Property;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.core.model.runtime.StateToCheck;
import com.yanry.driver.mobile.view.container.ViewContainer;
import com.yanry.driver.mobile.view.selector.ViewSelector;

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
