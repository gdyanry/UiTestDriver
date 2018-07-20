package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.property.CacheProperty;
import com.yanry.driver.core.model.property.Property;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.mobile.WindowManager;
import com.yanry.driver.mobile.action.EnterText;
import com.yanry.driver.mobile.view.container.ViewContainer;
import com.yanry.driver.mobile.view.selector.ViewSelector;

/**
 * Created by rongyu.yan on 5/9/2017.
 */
public class EditText extends View {
    private Content content;

    public EditText(Graph graph, ViewContainer parent, ViewSelector selector) {
        super(graph, parent, selector);
        content = new Content(graph);
    }

    public Content getContent() {
        return content;
    }

    public class Content extends CacheProperty<String> {

        public Content(Graph graph) {
            super(graph);
        }

        @Presentable
        public EditText getEditText() {
            return EditText.this;
        }

        @Override
        protected String checkValue() {
            if (getWindow().getManager().getCurrentWindow().getCurrentValue().equals(getWindow())) {
                return getGraph().fetchValue(this);
            }
            return null;
        }

        @Override
        protected boolean doSelfSwitch(String to) {
            return getWindow().getVisibility().switchTo(WindowManager.Visibility.Foreground) ||
                    getGraph().performAction(new EnterText(EditText.this, to));
        }

        @Override
        protected boolean equalsWithSameClass(Property<String> property) {
            Content content = (Content) property;
            return EditText.this.equals(content.getEditText());
        }
    }
}
