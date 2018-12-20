package com.yanry.driver.core.model.property;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.Set;
import java.util.stream.Stream;

public class CombinedProperty extends Property<StateSnapShoot> {
    private String name;
    private Property[] properties;

    public CombinedProperty(Graph graph, String name, Property... properties) {
        super(graph);
        this.name = name;
        this.properties = properties;
        for (Property property : properties) {
            property.addOnCleanListener(this::cleanCache);
            property.addOnChangeValueListener((o, n) -> refresh());
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
        for (Property property : properties) {
            ExternalEvent event = property.switchToValue(to.getValue(property));
            if (event != null) {
                return event;
            }
        }
        return null;
    }

    @Override
    protected Stream<StateSnapShoot> getValueStream(Set<StateSnapShoot> collectedValues) {
        return collectedValues.stream();
    }
}