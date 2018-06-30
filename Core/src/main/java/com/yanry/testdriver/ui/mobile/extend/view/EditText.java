package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.extend.WindowManager;
import com.yanry.testdriver.ui.mobile.extend.action.EnterText;
import com.yanry.testdriver.ui.mobile.extend.view.container.ViewContainer;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;

import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 5/9/2017.
 */
public class EditText extends View {
    private Content content;

    public EditText(ViewContainer parent, ViewSelector selector, Supplier<Boolean> defaultVisibility) {
        super(parent, selector, defaultVisibility);
        content = new Content();
    }

    public EditText(ViewContainer parent, ViewSelector selector) {
        this(parent, selector, null);
    }

    public Content getContent() {
        return content;
    }

    public class Content extends CacheProperty<String> {

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
            return getWindow().getVisibility().switchTo(graph, WindowManager.Visibility.Foreground, true) &&
                    graph.performAction(new EnterText(EditText.this, to));
        }
    }
}
