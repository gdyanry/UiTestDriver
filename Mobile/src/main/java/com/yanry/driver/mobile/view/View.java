/**
 *
 */
package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.property.CacheProperty;
import com.yanry.driver.core.model.property.Property;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.core.model.runtime.StateToCheck;
import com.yanry.driver.mobile.WindowManager;
import com.yanry.driver.mobile.view.container.ViewContainer;
import com.yanry.driver.mobile.view.selector.ViewSelector;

/**
 * @author yanry
 * <p>
 * Jan 10, 2017
 */
@Presentable
public class View {
    private ViewContainer parent;
    private ViewSelector selector;
    private ViewVisibility visibility;
    private Graph graph;

    public View(Graph graph, ViewContainer parent, ViewSelector selector) {
        this.graph = graph;
        this.parent = parent;
        this.selector = selector;
        visibility = new ViewVisibility(graph);
    }

    public WindowManager.Window getWindow() {
        if (parent instanceof WindowManager.Window) {
            return (WindowManager.Window) parent;
        } else if (parent instanceof View) {
            return ((View) parent).getWindow();
        }
        return null;
    }

    public ViewVisibility getVisibility() {
        return visibility;
    }

    public Graph getGraph() {
        return graph;
    }

    @Presentable
    public ViewContainer getParent() {
        return parent;
    }

    @Presentable
    public Object getSelector() {
        return selector;
    }

    public class ViewVisibility extends CacheProperty<Boolean> {

        public ViewVisibility(Graph graph) {
            super(graph);
        }

        @Presentable
        public View getView() {
            return View.this;
        }

        @Override
        protected Boolean checkValue() {
            return getGraph().checkState(new StateToCheck<>(this, false, true));
        }

        @Override
        protected boolean doSelfSwitch(Boolean to) {
            return false;
        }

        @Override
        protected boolean equalsWithSameClass(Property<Boolean> property) {
            ViewVisibility visibility = (ViewVisibility) property;
            return visibility.getView().equals(getView());
        }
    }
}