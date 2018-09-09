package com.yanry.driver.core.model.runtime.fetch;

import com.yanry.driver.core.Utils;
import com.yanry.driver.core.model.base.Property;

public class NonNegativeIntegerQuery extends Obtainable<Integer> {
    public NonNegativeIntegerQuery(Property<Integer> property) {
        super(property);
    }

    @Override
    public Integer convert(String fetchedValue) {
        if (Utils.isNonNegativeInteger(fetchedValue)) {
            return Integer.valueOf(fetchedValue);
        }
        return null;
    }
}
