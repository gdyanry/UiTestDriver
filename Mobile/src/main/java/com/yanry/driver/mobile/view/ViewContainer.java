package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.BooleanProperty;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.TransitionEvent;
import com.yanry.driver.mobile.view.selector.ById;

import java.util.HashMap;

/**
 * Created by rongyu.yan on 3/6/2017.
 */
public abstract class ViewContainer extends BooleanProperty {
    private HashMap<String, View> childViews;
    private TransitionEvent<Boolean> showEvent;
    private TransitionEvent<Boolean> dismissEvent;

    public ViewContainer(Graph graph) {
        super(graph);
        childViews = new HashMap<>();
        showEvent = new TransitionEvent<>(this, false, true);
        dismissEvent = new TransitionEvent<>(this, true, false);
    }

    public TransitionEvent<Boolean> getShowEvent() {
        return showEvent;
    }

    public TransitionEvent<Boolean> getDismissEvent() {
        return dismissEvent;
    }

    public View getViewById(String id) {
        View view = childViews.get(id);
        if (view == null) {
            view = new View(getGraph(), this, new ById(id));
            childViews.put(id, view);
        }
        return view;
    }
}
