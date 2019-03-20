package com.yanry.driver.core.model.runtime.fetch;

import com.yanry.driver.core.model.base.Property;
import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.Visible;

public class Select<V> extends Obtainable<V> {
    private Object[] options;

    public Select(Property<V> property) {
        super(property);
        this.options = property.getValues();
    }

    @Visible
    @EqualsPart
    public Object[] getOptions() {
        return options;
    }

    @Override
    public final V convert(String responseString) {
        if (responseString.matches("^([1-9]\\d*)|0|-1$")) {
            int index = Integer.parseInt(responseString);
            if (index < options.length) {
                return (V) options[index];
            }
        }
        return null;
    }
}
