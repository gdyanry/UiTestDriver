package com.yanry.driver.core.model.base;

import lib.common.util.object.Visible;
import lib.common.util.object.VisibleObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class ActionCollector extends VisibleObject {
    private LinkedList<ExternalEvent> list;
    private int limit;
    private HashSet<ExternalEvent> excluded;

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

    public boolean add(ExternalEvent event) {
        if (!list.contains(event) && list.size() < limit) {
            list.add(event);
            return true;
        }
        return false;
    }

    public void add(Iterator<ExternalEvent> iterator) {
        LinkedList<ExternalEvent> temp = new LinkedList<>();
        while (iterator.hasNext()) {
            if (list.size() + temp.size() < limit) {
                ExternalEvent next = iterator.next();
                if (!list.contains(next)) {
                    temp.add(next);
                }
            } else {
                break;
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

    public void exclude(ExternalEvent event) {
        if (excluded == null) {
            excluded = new HashSet<>();
        }
        excluded.add(event);
    }

    public boolean isExcluded(ExternalEvent event) {
        return excluded == null || !excluded.contains(event);
    }

    public ActionCollector getSubCollector(int limit) {
        ActionCollector collector = new ActionCollector(limit);
        collector.excluded = this.excluded;
        return collector;
    }

    @Visible
    public LinkedList<ExternalEvent> getList() {
        return list;
    }

    @Visible
    public int getLimit() {
        return limit;
    }
}
