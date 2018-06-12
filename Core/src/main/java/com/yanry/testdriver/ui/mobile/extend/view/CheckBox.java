package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.view.container.ViewContainer;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;

import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public class CheckBox extends TextView {
    private CheckState checkState;

    public CheckBox(ViewContainer parent, ViewSelector selector, Supplier<Boolean> defaultVisibility) {
        super(parent, selector, defaultVisibility);
        checkState = new CheckState();
    }

    public CheckBox(ViewContainer parent, ViewSelector selector) {
        this(parent, selector, null);
    }

    public CheckState getCheckState() {
        return checkState;
    }

    public class CheckState extends CacheProperty<Boolean> {

        @Presentable
        public CheckBox getCheckBox() {
            return CheckBox.this;
        }

        @Override
        protected Boolean checkValue(Graph graph) {
            return graph.checkState(new StateToCheck<>(this, false, true));
        }

        @Override
        protected boolean selfSwitch(Graph graph, Boolean to) {
            return getWindow().getVisibility().switchTo(graph, TestManager.Visibility.Foreground) &&
                    graph.performAction(new Click<>(CheckBox.this));
        }

        @Override
        public boolean isCheckedByUser() {
            return false;
        }
    }
}
