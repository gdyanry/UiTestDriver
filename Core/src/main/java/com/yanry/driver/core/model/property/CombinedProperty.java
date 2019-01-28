package com.yanry.driver.core.model.property;

import com.yanry.driver.core.model.base.ActionCollector;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.StateSpace;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.*;
import java.util.stream.Stream;

public class CombinedProperty extends Property<StateSnapShoot> {
    private String name;
    private Property[] properties;

    public CombinedProperty(StateSpace stateSpace, String name, Property... properties) {
        super(stateSpace);
        this.name = name;
        this.properties = properties;
        for (Property property : properties) {
            property.addOnCleanListener(this::cleanCache);
            property.addOnValueUpdateListener(v -> refresh());
        }
    }

    @EqualsPart
    @Visible
    public String getName() {
        return name;
    }

    @EqualsPart
    public Property[] getProperties() {
        return properties;
    }

    @Override
    protected StateSnapShoot checkValue() {
        StateSnapShoot.Builder builder = StateSnapShoot.builder();
        for (Property property : properties) {
            builder.append(property, property.getCurrentValue());
        }
        return builder.build();
    }

    @Override
    protected ExternalEvent doSelfSwitch(StateSnapShoot to) {
        LinkedHashMap<ExternalEvent, Integer> counter = new LinkedHashMap<>();
        for (Property property : properties) {
            Object toValue = to.getValue(property);
            if (!Objects.equals(toValue, property.getCurrentValue())) {
                ActionCollector actionCollector = new ActionCollector();
                property.switchToValue(toValue, actionCollector);
                Iterator<ExternalEvent> iterator = actionCollector.iterator();
                while (iterator.hasNext()) {
                    ExternalEvent externalEvent = iterator.next();
                    counter.put(externalEvent, counter.getOrDefault(externalEvent, 0) + 1);
                }
            }
        }
        Iterator<Map.Entry<ExternalEvent, Integer>> iterator = counter.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> -entry.getValue()))
                .iterator();
        if (iterator.hasNext()) {
            return iterator.next().getKey();
        }
        return null;
    }

    @Override
    protected Stream<StateSnapShoot> getValueStream(Set<StateSnapShoot> collectedValues) {
        return collectedValues.stream();
    }
}
