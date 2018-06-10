package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.action.EnterText;
import com.yanry.testdriver.ui.mobile.extend.view.container.ViewContainer;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;

import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 5/9/2017.
 */
public class EditText extends View {
    private InputContent inputContent;

    public EditText(ViewContainer parent, ViewSelector selector, Supplier<Boolean> defaultVisibility) {
        super(parent, selector, defaultVisibility);
        inputContent = new InputContent();
    }

    public EditText(ViewContainer parent, ViewSelector selector) {
        this(parent, selector, null);
    }

    public InputContent getInputContent() {
        return inputContent;
    }

    public class InputContent extends CacheProperty<String> {

        @Presentable
        public EditText getEditText() {
            return EditText.this;
        }

        @Override
        protected String checkValue(Graph graph) {
            return "";
        }

        @Override
        protected boolean doSelfSwitch(Graph graph, String to) {
            return getWindow().getVisibility().switchTo(graph, TestManager.Visibility.Foreground) &&
                    graph.performAction(new EnterText(EditText.this, to));
        }

        @Override
        public boolean isCheckedByUser() {
            return false;
        }
    }
}
