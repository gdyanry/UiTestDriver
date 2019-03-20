package com.yanry.driver.mobile.view.selector;

import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.Visible;

public class ById extends ViewSelector {
    private String id;

    public ById(String id) {
        this.id = id;
    }

    @Visible
    @EqualsPart
    public String getId() {
        return id;
    }
}
