package com.yanry.driver.core.model.runtime.revert;

import java.util.ArrayList;

public class RevertArrayListAdd<E> implements RevertStep {
    private ArrayList<E> list;
    private E element;

    public RevertArrayListAdd(ArrayList<E> list, E element) {
        this.list = list;
        this.element = element;
    }

    @Override
    public void proceed() {
        list.add(element);
    }

    @Override
    public void recover() {
        list.remove(list.size() - 1);
    }
}
