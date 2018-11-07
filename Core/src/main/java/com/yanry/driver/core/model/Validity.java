package com.yanry.driver.core.model;

import com.yanry.driver.core.model.base.*;
import com.yanry.driver.core.model.expectation.Timing;

/**
 * @Author: yanry
 * @Date: 2018/10/31 23:10
 */
public class Validity<V> extends BooleanProperty {
    private Property<V> property;
    private ValuePredicate<V> validPredicate;

    public Validity(Property<V> property, ValuePredicate<V> validPredicate) {
        super(property.getGraph());
        this.property = property;
        this.validPredicate = validPredicate;
        ValuePredicate<V> invalidPredicate = validPredicate.not();
        // -> false
        getGraph().addPath(new Path(new TransitionEvent<>(property, validPredicate, invalidPredicate), getStaticExpectation(Timing.IMMEDIATELY, false, false)));
        // -> true
        getGraph().addPath(new Path(new TransitionEvent<>(property, invalidPredicate, validPredicate), getStaticExpectation(Timing.IMMEDIATELY, false, true)));
    }

    public Validity(Graph graph) {
        super(graph);
    }

    @Override
    protected Boolean checkValue() {
        return validPredicate.test(property.getCurrentValue());
    }

    @Override
    protected ExternalEvent doSelfSwitch(Boolean to) {
        return null;
    }
}
