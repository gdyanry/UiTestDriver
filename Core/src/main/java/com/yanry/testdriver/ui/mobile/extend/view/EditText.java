package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
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

    public class InputContent extends SwitchBySelfProperty<String> {

        @Presentable
        public EditText getEditText() {
            return EditText.this;
        }

        @Override
        protected boolean dooSwitch(String to) {
            return getWindow().getVisibility().switchTo(TestManager.Visibility.Foreground) &&
                    getGraph().performAction(new EnterText(EditText.this, to));
        }

        @Override
        protected Graph getGraph() {
            return getWindow().getGraph();
        }

        @Override
        protected String checkValue() {
            return "";
        }
    }
}
