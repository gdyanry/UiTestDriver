package com.yanry.driver.mobile.sample.model;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.Watcher;

public class ConsoleWatcher implements Watcher {
    @Override
    public void onTransitionComplete() {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    @Override
    public <V> void onStateChange(Property<V> property, V fromVal, V toVal) {
        System.out.println(String.format(">>>>%s: %s -> %s", property, fromVal, toVal));
    }

}
