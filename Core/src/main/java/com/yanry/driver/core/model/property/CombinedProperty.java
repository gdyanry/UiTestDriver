package com.yanry.driver.core.model.property;

import com.yanry.driver.core.model.base.ActionFilter;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.StateSpace;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
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
    protected StateSnapShoot checkValue(StateSnapShoot expected) {
        StateSnapShoot.Builder builder = StateSnapShoot.builder();
        for (Property property : properties) {
            builder.append(property, property.getCurrentValue());
        }
        return builder.build();
    }

    @Override
    protected ExternalEvent doSelfSwitch(StateSnapShoot to, ActionFilter actionFilter) {
        return Arrays.stream(properties).filter(property -> !Objects.equals(to.getValue(property), property.getCurrentValue()))
                .map(property -> property.switchToValue(to.getValue(property), actionFilter))
                .filter(event -> event != null)
                .findAny().orElse(null);
    }

    @Override
    protected Stream<StateSnapShoot> getValueStream(Set<StateSnapShoot> collectedValues) {
        return collectedValues.stream();
    }
}
