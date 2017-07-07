/**
 *
 */
package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.SearchableSwitchableProperty;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.view.container.ViewContainer;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;

import java.util.function.Supplier;

/**
 * @author yanry
 *         <p>
 *         Jan 10, 2017
 */
@Presentable
public class View {
    private ViewContainer parent;
    private ViewSelector selector;
    private Supplier<Boolean> defaultVisibility;
    private ViewVisibility visibility;

    public View(ViewContainer parent, ViewSelector selector, Supplier<Boolean> defaultVisibility) {
        this.parent = parent;
        this.selector = selector;
        this.defaultVisibility = defaultVisibility;
        visibility = new ViewVisibility();
    }

    public View(ViewContainer parent, ViewSelector selector) {
        this(parent, selector, null);
    }

    public TestManager.Window getWindow() {
        if (parent instanceof TestManager.Window) {
            return (TestManager.Window) parent;
        } else if (parent instanceof View) {
            return ((View) parent).getWindow();
        }
        return null;
    }

    public ViewVisibility getVisibility() {
        return visibility;
    }

    @Presentable
    public ViewContainer getParent() {
        return parent;
    }

    @Presentable
    public Object getSelector() {
        return selector;
    }

    public class ViewVisibility extends SearchableSwitchableProperty<Boolean> {

        @Presentable
        public View getView() {
            return View.this;
        }

        @Override
        protected Boolean checkValue() {
            return defaultVisibility == null ? true : defaultVisibility.get();
        }

        @Override
        protected Graph getGraph() {
            return getWindow().getGraph();
        }

        @Override
        protected boolean isVisibleToUser() {
            return true;
        }
    }
}
