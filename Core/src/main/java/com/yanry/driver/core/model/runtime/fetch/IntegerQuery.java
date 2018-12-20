package com.yanry.driver.core.model.runtime.fetch;

import com.yanry.driver.core.model.base.Property;

public class IntegerQuery extends Obtainable<Integer> {
    public IntegerQuery(Property<Integer> property) {
        super(property);
    }

    @Override
    public Integer convert(String responseString) {
        try {
            return Integer.valueOf(responseString);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
