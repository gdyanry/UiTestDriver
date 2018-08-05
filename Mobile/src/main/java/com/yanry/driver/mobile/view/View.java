/**
 *
 */
package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.property.CacheProperty;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.core.model.runtime.StateToCheck;
import com.yanry.driver.mobile.WindowManager;
import com.yanry.driver.mobile.view.selector.ViewSelector;

/**
 * @author yanry
 * <p>
 * Jan 10, 2017
 */
@Presentable
public class View implements Visible {
    private ViewContainer parent;
    private ViewSelector selector;
    private IndependentVisibility independentVisibility;
    private Graph graph;

    public View(Graph graph, ViewContainer parent, ViewSelector selector) {
        this.graph = graph;
        this.parent = parent;
        this.selector = selector;
        independentVisibility = new IndependentVisibility(graph);
    }

    public WindowManager.Window getWindow() {
        if (parent instanceof WindowManager.Window) {
            return (WindowManager.Window) parent;
        } else if (parent instanceof View) {
            return ((View) parent).getWindow();
        }
        return null;
    }

    public IndependentVisibility getIndependentVisibility() {
        return independentVisibility;
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

    @Override
    public final boolean isVisible() {
        return parent.isVisible() && independentVisibility.getCurrentValue();
    }

    @Override
    public final boolean switchToVisible() {
        return parent.switchToVisible() || independentVisibility.switchTo(true);
    }

    public class IndependentVisibility extends CacheProperty<Boolean> {

        public IndependentVisibility(Graph graph) {
            super(graph);
        }

        @Presentable
        public View getView() {
            return View.this;
        }

        @Override
        protected Boolean checkValue() {
            return parent.isVisible() && getGraph().checkState(new StateToCheck<>(this, false, true));
        }

        @Override
        protected boolean doSelfSwitch(Boolean to) {
            return false;
        }
    }
}
