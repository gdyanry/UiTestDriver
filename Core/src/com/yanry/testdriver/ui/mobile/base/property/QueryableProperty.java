package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;

/**
 * Created by rongyu.yan on 4/26/2017.
 */
public class QueryableProperty extends SearchableProperty<String> {
    private Graph graph;
    private Object identifier;

    public QueryableProperty(Graph graph, Object identifier) {
        this.graph = graph;
        this.identifier = identifier;
    }

    @Presentable
    public Object getIdentifier() {
        return identifier;
    }

    @Override
    protected String checkValue() {
        return graph.queryValue(this);
    }

    @Override
    protected Graph getGraph() {
        return graph;
    }

    @Override
    protected boolean isVisibleToUser() {
        return true;
    }
}
