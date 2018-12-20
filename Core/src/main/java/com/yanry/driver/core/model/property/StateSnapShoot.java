package com.yanry.driver.core.model.property;

import com.yanry.driver.core.model.base.Property;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.HandyObject;
import lib.common.util.object.Visible;

import java.util.HashMap;

public class StateSnapShoot extends HandyObject {
    public static Builder builder() {
        return new Builder();
    }

    private HashMap<Property, Object> map;

    private StateSnapShoot(HashMap<Property, Object> map) {
        this.map = map;
    }

    public <V> V getValue(Property<V> property) {
        return (V) map.get(property);
    }

    @Visible
    @EqualsPart
    public HashMap<Property, Object> getMap() {
        return map;
    }

    public static class Builder {
        private HashMap<Property, Object> map;

        private Builder() {
            map = new HashMap<>();
        }

        public <V> Builder append(Property<V> property, V value) {
            map.put(property, value);
            return this;
        }

        public StateSnapShoot build() {
            return new StateSnapShoot(map);
        }
    }
}
