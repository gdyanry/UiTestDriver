package com.yanry.driver.core.model.runtime.fetch;

import com.yanry.driver.core.model.base.Property;

public class StringQuery extends Obtainable<String> {
    public StringQuery(Property<String> property) {
        super(property);
    }

    @Override
    public String convert(String fetchedValue) {
        return fetchedValue;
    }
}
