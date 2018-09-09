package com.yanry.driver.core.model.runtime.fetch;

import com.yanry.driver.core.model.base.Property;

public class BooleanQuery extends Select<Boolean> {
    public BooleanQuery(Property<Boolean> property) {
        super(property, false, true);
    }
}
