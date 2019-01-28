package com.yanry.driver.core.model.base;

import lib.common.util.object.Visible;
import lib.common.util.object.VisibleObject;

import java.util.*;

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

    public void trySatisfy(ActionCollector actionCollector, StateSpace stateSpace) {
        LinkedHashMap<ExternalEvent, Integer> counter = new LinkedHashMap<>();
        for (Map.Entry<Property, ValuePredicate> entry : states.entrySet()) {
            Property property = entry.getKey();
            ValuePredicate predicate = entry.getValue();
            if (!predicate.test(property.getCurrentValue())) {
                ActionCollector collector = new ActionCollector(1);
                property.switchTo(predicate, collector);
                if (collector.isEmpty()) {
                    return;
                } else {
                    Iterator<ExternalEvent> iterator = collector.iterator();
                    while (iterator.hasNext()) {
                        ExternalEvent externalEvent = iterator.next();
                        counter.put(externalEvent, counter.getOrDefault(externalEvent, 0) + 1);
                    }
                }
            }
        }
        actionCollector.add(counter.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> -entry.getValue()))
                .map(entry -> entry.getKey())
                .iterator(), stateSpace);
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
