package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.mobile.view.selector.ById;

import java.util.HashMap;

/**
 * Created by rongyu.yan on 3/6/2017.
 */
public abstract class ViewContainer {
    private HashMap<String, View> childViews;

    public ViewContainer() {
        childViews = new HashMap<>();
    }

    public View getViewById(String id) {
        View view = childViews.get(id);
        if (view == null) {
            view = new View(getGraph(), this, new ById(id));
            childViews.put(id, view);
        }
        return view;
    }

    protected abstract Graph getGraph();

    public abstract boolean isVisible();

    /**
     * @return 是否触发ActionEvent
     */
    public abstract boolean switchToVisible();
}
