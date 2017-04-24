package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;

import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 4/24/2017.
 */
public interface StatelessProperty<V> {
    default StatelessPropertyExpectation<V> getExpectation(Graph graph, Timing timing, Supplier<V> value) {
        return new StatelessPropertyExpectation<V>(timing, this, value) {
            @Override
            protected Graph getGraph() {
                return graph;
            }
        };
    }
}
