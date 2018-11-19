package com.yanry.driver.mobile.view.listview;

import com.yanry.driver.mobile.property.ViewIntProperty;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ListViewSize extends ViewIntProperty implements Supplier<Integer> {
    private int num;

    public ListViewSize(ListView view) {
        super(view);
    }

    @Override
    protected Stream<Integer> getValueStream(Set<Integer> collectedValues) {
        num = 0;
        return Stream.generate(this);
    }

    @Override
    public Integer get() {
        return num++;
    }
}
