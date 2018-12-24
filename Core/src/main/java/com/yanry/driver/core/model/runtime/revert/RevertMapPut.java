package com.yanry.driver.core.model.runtime.revert;

import java.util.Map;

public class RevertMapPut<K, V> implements RevertStep {
    private Map<K, V> map;
    private K key;
    private V value;
    private V oldValue;

    public RevertMapPut(Map<K, V> map, K key, V value) {
        this.map = map;
        this.key = key;
        this.value = value;
    }

    @Override
    public void proceed() {
        oldValue = map.put(key, value);
    }

    @Override
    public void recover() {
        map.put(key, oldValue);
    }
}
