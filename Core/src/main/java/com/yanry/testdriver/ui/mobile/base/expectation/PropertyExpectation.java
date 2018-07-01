package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.Property;

/**
 * A key-value pair (aka state) expectation
 * Created by rongyu.yan on 5/10/2017.
 */
public abstract class PropertyExpectation<V> extends Expectation {
    private Property<V> property;

    public PropertyExpectation(Timing timing, boolean needCheck, Property<V> property) {
        super(timing, needCheck);
        this.property = property;
    }

    public abstract V getExpectedValue();

    @Override
    protected final boolean selfVerify(Graph graph) {
        V expectedValue = getExpectedValue();
        property.handleExpectation(expectedValue, isNeedCheck());
        return expectedValue.equals(property.getCurrentValue(graph));
    }

    @Override
    protected int getMatchDegree(Graph graph, Path path) {
        Object value = path.get(property);
        return value != null && !value.equals(property.getCurrentValue(graph)) && value.equals(getExpectedValue()) ? 100 : 0;
    }

    @Presentable
    public Property<V> getProperty() {
        return property;
    }
}
