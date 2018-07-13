package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.property.Property;
import com.yanry.testdriver.ui.mobile.extend.WindowManager;
import com.yanry.testdriver.ui.mobile.extend.action.EnterText;
import com.yanry.testdriver.ui.mobile.extend.view.container.ViewContainer;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;

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
