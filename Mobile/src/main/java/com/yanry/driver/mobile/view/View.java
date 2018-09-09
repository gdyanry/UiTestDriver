/**
 *
 */
package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.base.CacheProperty;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.event.StateEvent;
import com.yanry.driver.core.model.expectation.SSPropertyExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.core.model.runtime.fetch.BooleanQuery;
import com.yanry.driver.mobile.view.selector.ViewSelector;
import com.yanry.driver.mobile.window.Window;

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
        getWindow().createPath(parent.getShowEvent(), showExpectation)
                .addInitState(independentVisibility, true);
        getWindow().createPath(new StateEvent<>(independentVisibility, false, true), showExpectation)
                .addInitState(parent, true);
        SSPropertyExpectation<Boolean> dismissExpectation = getStaticExpectation(Timing.IMMEDIATELY, false, false);
        getWindow().createPath(parent.getDismissEvent(), dismissExpectation)
                .addInitState(independentVisibility, true);
        getWindow().createPath(new StateEvent<>(independentVisibility, true, false), dismissExpectation)
                .addInitState(parent, true);
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
    public void handleExpectation(Boolean expectedValue, boolean needCheck) {

    }

    @Override
    public Boolean getCurrentValue() {
        return parent.getCurrentValue() && independentVisibility.getCurrentValue();
    }

    @Override
    protected boolean selfSwitch(Boolean to) {
        return false;
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
            return getGraph().obtainValue(new BooleanQuery(this));
        }

        @Override
        protected SwitchResult doSelfSwitch(Boolean to) {
            return SwitchResult.NoAction;
        }
    }
}
