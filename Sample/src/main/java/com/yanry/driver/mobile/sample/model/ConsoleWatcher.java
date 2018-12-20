package com.yanry.driver.mobile.sample.model;

import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.Watcher;

import java.util.Map;
import java.util.Set;

public class ConsoleWatcher implements Watcher {
    @Override
    public void onTransitionComplete(Map<Property, Object> propertyCache, Set<Property> nullCache, Set<Path> verifiedPaths) {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    @Override
    public <V> void onStateChange(Property<V> property, V fromVal, V toVal) {
        System.out.println(String.format(">>>>%s: %s -> %s", property, fromVal, toVal));
    }

}
