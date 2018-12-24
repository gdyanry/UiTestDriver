package com.yanry.driver.core.model.runtime.revert;

import java.util.LinkedList;

public class RevertLinkedList<E> implements RevertStep {
    public static final int ADD_LAST = 0;
    public static final int ADD_FIRST = 1;

    private LinkedList<E> list;
    private E element;
    private int operation;

    public RevertLinkedList(LinkedList<E> list, E element, int operation) {
        this.list = list;
        this.element = element;
        this.operation = operation;
    }

    @Override
    public void proceed() {
        switch (operation) {
            case ADD_FIRST:
                list.addFirst(element);
                break;
            case ADD_LAST:
                list.addLast(element);
                break;
        }
    }

    @Override
    public void recover() {
        switch (operation) {
            case ADD_FIRST:
                list.removeFirst();
                break;
            case ADD_LAST:
                list.removeLast();
                break;
        }
    }
}
