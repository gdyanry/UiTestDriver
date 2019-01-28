package com.yanry.driver.core.model.base;

import lib.common.util.object.Visible;
import lib.common.util.object.VisibleObject;

import java.util.Iterator;
import java.util.LinkedList;

public class ActionCollector extends VisibleObject {
    private LinkedList<ExternalEvent> list;
    private int limit;
    private Context promises;

    public ActionCollector() {
        this(128);
    }

    public ActionCollector(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be greater than 0");
        }
        this.limit = limit;
        list = new LinkedList<>();
    }

    public <V> void addPromise(Property<V> property, ValuePredicate<V> predicate) {
        if (promises == null) {
            promises = new Context();
        }
        promises.add(property, predicate);
    }

    public ExternalEvent pop() {
        Iterator<ExternalEvent> iterator = list.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public Iterator<ExternalEvent> iterator() {
        return list.iterator();
    }

    public boolean add(ExternalEvent event, StateSpace stateSpace) {
        if (!list.contains(event) && list.size() < limit && testPromises(event, stateSpace)) {
            list.add(event);
            return true;
        }
        return false;
    }

    private boolean testPromises(ExternalEvent event, StateSpace stateSpace) {
        if (promises != null) {
            stateSpace.tag(this);
            stateSpace.fire(event);
            if (!promises.isSatisfied()) {
                stateSpace.revertTo(this);
                return false;
            }
            stateSpace.revertTo(this);
        }
        return true;
    }

    public void add(Iterator<ExternalEvent> iterator, StateSpace stateSpace) {
        LinkedList<ExternalEvent> temp = new LinkedList<>();
        while (iterator.hasNext()) {
            if (list.size() < limit) {
                ExternalEvent next = iterator.next();
                if (!list.contains(next) && testPromises(next, stateSpace)) {
                    temp.add(next);
                }
            }
        }
        list.addAll(temp);
    }

    public boolean isFull() {
        return list.size() >= limit;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Visible
    public LinkedList<ExternalEvent> getList() {
        return list;
    }

    @Visible
    public int getLimit() {
        return limit;
    }

    @Visible
    public Context getPromises() {
        return promises;
    }
}
