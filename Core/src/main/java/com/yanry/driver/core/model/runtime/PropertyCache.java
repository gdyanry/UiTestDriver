package com.yanry.driver.core.model.runtime;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.revert.*;
import lib.common.model.log.Logger;
import lib.common.util.object.ObjectUtil;

import java.util.HashMap;
import java.util.HashSet;

public class PropertyCache {
    private HashSet<Property> nullCache;
    private HashMap<Property, Object> valueCache;
    private RevertManager revertManager;

    public PropertyCache(RevertManager revertManager) {
        this.revertManager = revertManager;
        nullCache = new HashSet<>();
        valueCache = new HashMap<>();
    }

    public boolean hasCache(Property property) {
        return nullCache.contains(property) || valueCache.containsKey(valueCache);
    }

    public <V> boolean updateCache(Property<V> property, V value) {
        if (value == null) {
            if (valueCache.containsKey(property)) {
                revertManager.proceed(new RevertMapRemove<>(valueCache, property));
            }
            if (nullCache.contains(property)) {
                return false;
            } else {
                revertManager.proceed(new RevertSet<>(nullCache, property, RevertSet.ADD));
                return true;
            }
        } else {
            if (nullCache.contains(property)) {
                revertManager.proceed(new RevertSet<>(nullCache, property, RevertSet.REMOVE));
            }
            Object oldValue = valueCache.get(property);
            if (value.equals(oldValue)) {
                return false;
            } else {
                revertManager.proceed(new RevertMapPut<>(valueCache, property, value));
                return true;
            }
        }
    }

    public boolean clean(Property property) {
        if (nullCache.contains(property)) {
            revertManager.proceed(new RevertSet<>(nullCache, property, RevertSet.REMOVE));
            return true;
        }
        if (valueCache.containsKey(property)) {
            revertManager.proceed(new RevertMapRemove<>(valueCache, property));
            return true;
        }
        return false;
    }

    public <V> V getCache(Property<V> property) {
        return (V) valueCache.get(property);
    }

    public void clearAll() {
        revertManager.proceed(new RevertStep() {
            private HashMap<Property, Object> valueCacheBackup;
            private HashSet<Property> nullCacheBackup;

            @Override
            public void proceed() {
                valueCacheBackup = valueCache;
                nullCacheBackup = nullCache;
                valueCache = new HashMap<>();
                nullCache = new HashSet<>();
            }

            @Override
            public void recover() {
                valueCache = valueCacheBackup;
                nullCache = nullCacheBackup;
            }
        });
    }

    public String getSnapShootMD5() {
        try {
            return ObjectUtil.getSnapShootMd5(valueCache) + ObjectUtil.getSnapShootMd5(nullCache);
        } catch (Exception e) {
            Logger.getDefault().catches(e);
            return null;
        }
    }
}
