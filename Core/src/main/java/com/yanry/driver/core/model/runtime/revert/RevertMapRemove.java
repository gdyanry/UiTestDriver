package com.yanry.driver.core.model.runtime.revert;

import java.util.Map;

public class RevertMapRemove<K, V> implements RevertStep {
    private Map<K, V> map;
    private K key;
    private V value;

    public RevertMapRemove(Map<K, V> map, K key) {
        this.map = map;
        this.key = key;
    }

    @Override
    public void proceed() {
        value = map.remove(key);
    }

    @Override
    public void recover() {
        map.put(key, value);
    }
}
