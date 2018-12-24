package com.yanry.driver.core.model.runtime.revert;

import java.util.Set;

public class RevertSet<E> implements RevertStep {
    public static final int ADD = 0;
    public static final int REMOVE = 1;

    private Set<E> set;
    private E element;
    private int operation;
    private boolean done;

    public RevertSet(Set<E> set, E element, int operation) {
        this.set = set;
        this.element = element;
        this.operation = operation;
    }

    @Override
    public void proceed() {
        switch (operation) {
            case ADD:
                done = set.add(element);
                break;
            case REMOVE:
                done = set.remove(element);
                break;
        }
    }

    @Override
    public void recover() {
        if (done) {
            switch (operation) {
                case ADD:
                    set.remove(element);
                    break;
                case REMOVE:
                    set.add(element);
                    break;
            }
        }
    }
}
