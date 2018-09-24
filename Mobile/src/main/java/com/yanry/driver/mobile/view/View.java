/**
 *
 */
package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.event.TransitionEvent;
import com.yanry.driver.core.model.expectation.SSPropertyExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.runtime.fetch.BooleanQuery;
import com.yanry.driver.mobile.view.selector.ViewSelector;
import com.yanry.driver.mobile.window.Window;
import lib.common.util.object.Presentable;

/**
 * @author yanry
 * <p>
 * Jan 10, 2017
 */
@Presentable
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
        independentVisibility.handleExpectation(true, false);
        SSPropertyExpectation<Boolean> showExpectation = getStaticExpectation(Timing.IMMEDIATELY, false, true);
        getWindow().createForegroundPath(parent.getShowEvent(), showExpectation)
                .addContextState(independentVisibility, true);
        getWindow().createForegroundPath(new TransitionEvent<>(independentVisibility, false, true), showExpectation)
                .addContextState(parent, true);
        SSPropertyExpectation<Boolean> dismissExpectation = getStaticExpectation(Timing.IMMEDIATELY, false, false);
        getWindow().createForegroundPath(parent.getDismissEvent(), dismissExpectation)
                .addContextState(independentVisibility, true);
        getWindow().createForegroundPath(new TransitionEvent<>(independentVisibility, true, false), dismissExpectation)
                .addContextState(parent, true);
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

    @Presentable
    public ViewContainer getParent() {
        return parent;
    }

    @Presentable
    public Object getSelector() {
        return selector;
    }

    @Override
    protected Boolean fetchValue() {
        return parent.getCurrentValue() && independentVisibility.getCurrentValue();
    }

    @Override
    protected ActionEvent doSelfSwitch(Boolean to) {
        return null;
    }

    public class IndependentVisibility extends Property<Boolean> {

        public IndependentVisibility(Graph graph) {
            super(graph);
        }

        @Presentable
        public View getView() {
            return View.this;
        }

        @Override
        protected Boolean fetchValue() {
            return getGraph().obtainValue(new BooleanQuery(this));
        }

        @Override
        protected ActionEvent doSelfSwitch(Boolean to) {
            return null;
        }
    }
}
