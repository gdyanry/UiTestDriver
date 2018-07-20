package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.property.CacheProperty;
import com.yanry.driver.core.model.property.Property;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.core.model.runtime.StateToCheck;
import com.yanry.driver.mobile.WindowManager;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.view.container.ViewContainer;
import com.yanry.driver.mobile.view.selector.ViewSelector;

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
