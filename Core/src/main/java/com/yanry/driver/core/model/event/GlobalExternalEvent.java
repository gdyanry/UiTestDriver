package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.ExternalEvent;
import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.Visible;

import java.util.HashMap;

public class GlobalExternalEvent extends ExternalEvent {
    private static HashMap<Object, GlobalExternalEvent> cache = new HashMap<>();

    public static GlobalExternalEvent get(Object id) {
        GlobalExternalEvent event = cache.get(id);
        if (event == null) {
            synchronized (GlobalExternalEvent.class) {
                event = cache.get(id);
                if (event == null) {
                    event = new GlobalExternalEvent(id);
                    cache.put(id, event);
                }
            }
        }
        return event;
    }

    private Object id;

    private GlobalExternalEvent(Object id) {
        this.id = id;
    }

    @Visible
    @EqualsPart
    public Object getId() {
        return id;
    }
}
