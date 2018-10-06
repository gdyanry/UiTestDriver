package com.yanry.driver.core.model.runtime.fetch;

import com.yanry.driver.core.model.base.Property;

public class NonNegativeIntegerQuery extends Obtainable<Integer> {
    public NonNegativeIntegerQuery(Property<Integer> property) {
        super(property);
    }

    @Override
    public Integer convert(String responseString) {
        if (responseString.matches("^([1-9]\\d*)|0$")) {
            return Integer.valueOf(responseString);
        }
        return null;
    }
}
