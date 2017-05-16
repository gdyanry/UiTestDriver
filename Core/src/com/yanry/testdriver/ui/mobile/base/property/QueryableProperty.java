package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.expectation.ActionExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.NotSwitchablePropertyExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 4/26/2017.
 */
@Presentable
public class QueryableProperty implements Property {
    private String value;
    private Object identifier;

    public QueryableProperty(Object identifier) {
        this.identifier = identifier;
    }

    public NotSwitchablePropertyExpectation<String> getStaticExpectation(Graph graph, Timing timing, String value) {
        return new NotSwitchablePropertyExpectation<String>(timing, this, value) {
            @Override
            protected Graph getGraph() {
                return graph;
            }
        };
    }

    public NotSwitchablePropertyExpectation<String> getDynamicExpectation(Graph graph, Timing timing, Supplier<String>
            valueSupplier) {
        return new NotSwitchablePropertyExpectation<String>(timing, this, valueSupplier) {
            @Override
            protected Graph getGraph() {
                return graph;
            }
        };
    }

    public Consumer<List<Path>> asFollowingAction(Graph graph) {
        return superPaths -> doQuery(graph);
    }

    public ActionExpectation asActionExpectation(Graph graph) {
        return new ActionExpectation() {
            @Override
            public void run() {
                doQuery(graph);
            }
        };
    }

    public boolean hasValue() {
        return value != null;
    }

    public String getValue(boolean abandon) {
        if (abandon) {
            String v = value;
            value = null;
            return v;
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void doQuery(Graph graph) {
        value = graph.queryValue(this);
    }

    @Presentable
    public Object getIdentifier() {
        return identifier;
    }
}
