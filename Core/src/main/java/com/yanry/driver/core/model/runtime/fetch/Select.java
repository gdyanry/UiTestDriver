package com.yanry.driver.core.model.runtime.fetch;

import com.yanry.driver.core.model.base.Property;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

public class Select<V> extends Obtainable<V> {
    private V[] options;

    public Select(Property<V> property, V... options) {
        super(property);
        this.options = options;
    }

    @Visible
    @EqualsPart
    public V[] getOptions() {
        return options;
    }

    @Override
    public final V convert(String responseString) {
        if (responseString.matches("^([1-9]\\d*)|0$")) {
            int index = Integer.parseInt(responseString);
            if (index < options.length) {
                return options[index];
            }
        }
        return null;
    }
}
