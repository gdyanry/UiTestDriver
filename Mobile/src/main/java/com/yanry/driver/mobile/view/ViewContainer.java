package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.event.StateEvent;
import com.yanry.driver.mobile.view.selector.ById;

import java.util.HashMap;

/**
 * Created by rongyu.yan on 3/6/2017.
 */
public abstract class ViewContainer extends Property<Boolean> {
    private HashMap<String, View> childViews;
    private StateEvent<Boolean> showEvent;
    private StateEvent<Boolean> dismissEvent;

    public ViewContainer(Graph graph) {
        super(graph);
        childViews = new HashMap<>();
        showEvent = new StateEvent<>(this, false, true);
        dismissEvent = new StateEvent<>(this, true, false);
    }

    public StateEvent<Boolean> getShowEvent() {
        return showEvent;
    }

    public StateEvent<Boolean> getDismissEvent() {
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
