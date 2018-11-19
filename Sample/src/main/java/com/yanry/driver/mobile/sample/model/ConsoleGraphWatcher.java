package com.yanry.driver.mobile.sample.model;

import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.GraphWatcher;

import java.util.Map;
import java.util.Set;

public class ConsoleGraphWatcher implements GraphWatcher {
    @Override
    public void onStandby(Map<Property, Object> propertyCache, Set<Property> nullCache, Set<Path> verifiedPaths) {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        for (Property property : propertyCache.keySet()) {
            System.out.println(String.format(">>>>%s - %s", property, property.getCurrentValue()));
        }
        for (Property property : nullCache) {
            System.out.println(String.format(">>>>%s - null", property));
        }
//        System.out.println("verified paths:");
//        for (Path path : verifiedPaths) {
//            System.out.println("    " + path);
//        }
        System.out.println("------------------------------------------------------------------------------------------");
    }

}
