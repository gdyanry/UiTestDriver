package com.yanry.driver.core.model.runtime.fetch;

import com.yanry.driver.core.Utils;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.Presentable;

public class Select<V> extends Obtainable<V> {
    private V[] options;

    public Select(Property<V> property, V... options) {
        super(property);
        this.options = options;
    }

    @Presentable
    public V[] getOptions() {
        return options;
    }

    @Override
    public final V convert(String fetchedValue) {
        if (Utils.isNonNegativeInteger(fetchedValue)) {
            int index = Integer.parseInt(fetchedValue);
            if (index < options.length) {
                return options[index];
            }
        }
        return null;
    }
}
