/**
 *
 */
package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.SSPropertyExpectation;
import com.yanry.driver.core.model.base.TransitionEvent;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.extension.BooleanProperty;
import com.yanry.driver.core.model.runtime.fetch.BooleanQuery;
import com.yanry.driver.mobile.view.selector.ViewSelector;
import com.yanry.driver.mobile.window.Window;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

/**
 * @author yanry
 * <p>
 * Jan 10, 2017
 */
public class View extends ViewContainer {
    private ViewContainer parent;
    private ViewSelector selector;
    private IndependentVisibility independentVisibility;

    public View(Graph graph, ViewContainer parent, ViewSelector selector) {
        super(graph);
        this.parent = parent;
        this.selector = selector;
        independentVisibility = new IndependentVisibility(graph);
        // 默认可见
        independentVisibility.setInitValue(true);
        SSPropertyExpectation<Boolean> showExpectation = getStaticExpectation(Timing.IMMEDIATELY, false, true);
        // false -> true
        graph.createPath(parent.onShow(), showExpectation)
                .addContextValue(independentVisibility, true);
        graph.createPath(new TransitionEvent<>(independentVisibility, false, true), showExpectation)
                .addContextValue(parent, true);
        SSPropertyExpectation<Boolean> dismissExpectation = getStaticExpectation(Timing.IMMEDIATELY, false, false);
        // true -> false
        graph.createPath(parent.onDismiss(), dismissExpectation)
                .addContextValue(independentVisibility, true);
        graph.createPath(new TransitionEvent<>(independentVisibility, true, false), dismissExpectation)
                .addContextValue(parent, true);
        // cleanCache
        parent.addOnCleanListener(() -> cleanCache());
    }

    public Window getWindow() {
        if (parent instanceof Window) {
            return (Window) parent;
        } else if (parent instanceof View) {
            return ((View) parent).getWindow();
        }
        throw new IllegalStateException("view is not contained in a window.");
    }

    public IndependentVisibility getIndependentVisibility() {
        return independentVisibility;
    }

    @Visible
    @EqualsPart
    public ViewContainer getParent() {
        return parent;
    }

    @Visible
    @EqualsPart
    public ViewSelector getSelector() {
        return selector;
    }

    @Override
    protected Boolean checkValue() {
        return parent.getCurrentValue() && independentVisibility.getCurrentValue();
    }

    @Override
    protected ExternalEvent doSelfSwitch(Boolean to) {
        return null;
    }

    public class IndependentVisibility extends BooleanProperty {

        public IndependentVisibility(Graph graph) {
            super(graph);
        }

        @Visible
        @EqualsPart
        public View getView() {
            return View.this;
        }

        @Override
        protected Boolean checkValue() {
            return getGraph().obtainValue(new BooleanQuery(this));
        }

        @Override
        protected ExternalEvent doSelfSwitch(Boolean to) {
            return null;
        }
    }
}
