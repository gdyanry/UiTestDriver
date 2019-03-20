/**
 *
 */
package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.base.*;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.property.BooleanProperty;
import com.yanry.driver.core.model.runtime.fetch.BooleanQuery;
import com.yanry.driver.mobile.view.selector.ViewSelector;
import com.yanry.driver.mobile.window.Window;
import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.Visible;

/**
 * @author yanry
 * <p>
 * Jan 10, 2017
 */
public class View extends ViewContainer {
    private ViewContainer parent;
    private ViewSelector selector;
    private IndependentVisibility independentVisibility;

    public View(StateSpace stateSpace, ViewContainer parent, ViewSelector selector) {
        super(stateSpace);
        this.parent = parent;
        this.selector = selector;
        independentVisibility = new IndependentVisibility(stateSpace);
        // 默认可见
        independentVisibility.setInitValue(true);
        SSPropertyExpectation<Boolean> showExpectation = getStaticExpectation(Timing.IMMEDIATELY, false, true);
        // false -> true
        stateSpace.createPath(parent.onShow(), showExpectation)
                .addContextValue(independentVisibility, true);
        stateSpace.createPath(new TransitionEvent<>(independentVisibility, false, true), showExpectation)
                .addContextValue(parent, true);
        SSPropertyExpectation<Boolean> dismissExpectation = getStaticExpectation(Timing.IMMEDIATELY, false, false);
        // true -> false
        stateSpace.createPath(parent.onDismiss(), dismissExpectation)
                .addContextValue(independentVisibility, true);
        stateSpace.createPath(new TransitionEvent<>(independentVisibility, true, false), dismissExpectation)
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
    protected Boolean checkValue(Boolean expected) {
        return parent.getCurrentValue() && independentVisibility.getCurrentValue();
    }

    @Override
    protected ExternalEvent doSelfSwitch(Boolean to, ActionGuard actionGuard) {
        return null;
    }

    public class IndependentVisibility extends BooleanProperty {

        public IndependentVisibility(StateSpace stateSpace) {
            super(stateSpace);
        }

        @Visible
        @EqualsPart
        public View getView() {
            return View.this;
        }

        @Override
        protected Boolean checkValue(Boolean expected) {
            return getStateSpace().obtainValue(new BooleanQuery(this), expected);
        }

        @Override
        protected ExternalEvent doSelfSwitch(Boolean to, ActionGuard actionGuard) {
            return null;
        }
    }
}
