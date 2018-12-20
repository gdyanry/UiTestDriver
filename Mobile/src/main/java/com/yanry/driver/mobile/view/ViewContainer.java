package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.TransitionEvent;
import com.yanry.driver.core.model.extension.BooleanProperty;
import com.yanry.driver.mobile.view.selector.ById;
import com.yanry.driver.mobile.view.selector.ViewSelector;

import java.util.HashMap;

/**
 * Created by rongyu.yan on 3/6/2017.
 */
public abstract class ViewContainer extends BooleanProperty {
    private HashMap<ViewSelector, View> childViews;
    private TransitionEvent<Boolean> showEvent;
    private TransitionEvent<Boolean> dismissEvent;

    public ViewContainer(Graph graph) {
        super(graph);
        childViews = new HashMap<>();
        showEvent = new TransitionEvent<>(this, false, true);
        dismissEvent = new TransitionEvent<>(this, true, false);
    }

    public TransitionEvent<Boolean> onShow() {
        return showEvent;
    }

    public TransitionEvent<Boolean> onDismiss() {
        return dismissEvent;
    }

    public View getView(ViewSelector selector) {
        View view = childViews.get(selector);
        if (view == null) {
            view = new View(getGraph(), this, selector);
            childViews.put(selector, view);
        }
        return view;
    }

    public View getViewById(String id) {
        return getView(new ById(id));
    }
}
