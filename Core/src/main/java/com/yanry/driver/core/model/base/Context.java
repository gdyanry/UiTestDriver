package com.yanry.driver.core.model.base;

import yanry.lib.java.util.object.Visible;
import yanry.lib.java.util.object.VisibleObject;

import java.util.HashMap;
import java.util.Map;

public class Context extends VisibleObject {
    private HashMap<Property, ValuePredicate> states;
    private long graphFrameMark;
    private int unsatisfiedDegree;

    public Context() {
        states = new HashMap<>();
    }

    public <V> void add(Property<V> property, ValuePredicate<V> valuePredicate) {
        property.findValueToAdd(valuePredicate);
        ValuePredicate existPredicate = states.get(property);
        if (existPredicate != null) {
            existPredicate.and(valuePredicate);
        } else {
            states.put(property, valuePredicate);
        }
    }

    public boolean isSatisfied() {
        for (Map.Entry<Property, ValuePredicate> entry : states.entrySet()) {
            if (!entry.getValue().test(entry.getKey().getCurrentValue())) {
                return false;
            }
        }
        return true;
    }

    public ExternalEvent trySatisfy(ActionGuard actionGuard) {
        for (Map.Entry<Property, ValuePredicate> entry : states.entrySet()) {
            Property property = entry.getKey();
            ValuePredicate predicate = entry.getValue();
            if (!predicate.test(property.getCurrentValue())) {
                return property.switchTo(predicate, actionGuard);
            }
        }
        return null;
    }

    public int getUnsatisfiedDegree(long currentFrameMark, Property excludeProperty, int stepLength) {
        if (currentFrameMark == 0 || currentFrameMark != this.graphFrameMark) {
            unsatisfiedDegree = states.keySet().stream()
                    .filter(property -> !property.equals(excludeProperty) && !states.get(property).test(property.getCurrentValue()))
                    .mapToInt(prop -> stepLength).sum();
            this.graphFrameMark = currentFrameMark;
        }
        return unsatisfiedDegree;
    }

    @Visible
    public HashMap<Property, ValuePredicate> getStates() {
        return states;
    }
}
