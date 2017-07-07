package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.expectation.VerifyValuePropertyExpectation;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;

import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 4/26/2017.
 */
@Presentable
public class QueryableProperty implements Property<String> {
    private String value;
    private Graph graph;
    private Object identifier;

    public QueryableProperty(Graph graph, Object identifier) {
        this.graph = graph;
        this.identifier = identifier;
    }

    public void clearValue() {
        value = null;
    }

    public VerifyValuePropertyExpectation getExpectation(Timing timing, String value) {
        return new VerifyValuePropertyExpectation(timing, this, value);
    }

    public VerifyValuePropertyExpectation getExpectation(Timing timing, Supplier<String> valueSupplier) {
        return new VerifyValuePropertyExpectation(timing, this, valueSupplier);
    }

    @Presentable
    public Object getIdentifier() {
        return identifier;
    }

    @Override
    public String getCurrentValue() {
        if (value == null) {
            value = graph.queryValue(this);
        }
        return value;
    }
}