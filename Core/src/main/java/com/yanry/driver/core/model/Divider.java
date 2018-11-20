package com.yanry.driver.core.model;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.ValuePredicate;
import com.yanry.driver.core.model.event.NegationEvent;
import com.yanry.driver.core.model.expectation.Timing;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

/**
 * @Author: yanry
 * @Date: 2018/10/31 23:10
 */
public class Divider<V> extends BooleanProperty {
    private Property<V> property;
    private ValuePredicate<V> predicate;

    public Divider(Property<V> property, ValuePredicate<V> predicate) {
        super(property.getGraph());
        this.property = property;
        this.predicate = predicate;
        // -> false
        getGraph().createPath(new NegationEvent<>(property, predicate),
                getStaticExpectation(Timing.IMMEDIATELY, false, false));
        // -> true
        getGraph().createPath(new NegationEvent<>(property, predicate.not()),
                getStaticExpectation(Timing.IMMEDIATELY, false, true));
        // clean
        property.addOnCleanListener(() -> clean());
    }

    @EqualsPart
    @Visible
    public Property<V> getProperty() {
        return property;
    }

    @EqualsPart
    @Visible
    public ValuePredicate<V> getPredicate() {
        return predicate;
    }

    @Override
    protected Boolean checkValue() {
        return predicate.test(property.getCurrentValue());
    }

    @Override
    protected ExternalEvent doSelfSwitch(Boolean to) {
        return null;
    }
}
