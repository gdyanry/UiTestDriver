package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.property.Property;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.extend.WindowManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.view.container.ViewContainer;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;

import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public class CheckBox extends TextView {
    private CheckState checkState;

    public CheckBox(Graph graph, ViewContainer parent, ViewSelector selector) {
        super(graph, parent, selector);
        checkState = new CheckState(graph);
    }

    public CheckState getCheckState() {
        return checkState;
    }

    public class CheckState extends CacheProperty<Boolean> {

        public CheckState(Graph graph) {
            super(graph);
        }

        @Presentable
        public CheckBox getCheckBox() {
            return CheckBox.this;
        }

        @Override
        protected Boolean checkValue() {
            return getGraph().checkState(new StateToCheck<>(this, false, true));
        }

        @Override
        protected boolean doSelfSwitch(Boolean to) {
            return getWindow().getVisibility().switchTo(WindowManager.Visibility.Foreground) ||
                    getGraph().performAction(new Click<>(CheckBox.this));
        }

        @Override
        protected boolean equalsWithSameClass(Property<Boolean> property) {
            CheckState checkState = (CheckState) property;
            return CheckBox.this.equals(checkState.getCheckBox());
        }
    }
}
