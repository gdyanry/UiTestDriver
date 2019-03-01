package com.yanry.driver.core.model.base;

import lib.common.util.object.Visible;
import lib.common.util.object.VisibleObject;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Predicate;

public class ActionFilter extends VisibleObject {
    private HashSet<ExternalEvent> invalidActions;
    private LinkedList<Predicate<ExternalEvent>> filters;

    public ActionFilter() {
        invalidActions = new HashSet<>();
    }

    public void addFilter(Predicate<ExternalEvent> filter) {
        if (filters == null) {
            filters = new LinkedList<>();
        }
        filters.add(filter);
    }

    public void invalidate(ExternalEvent event) {
        invalidActions.add(event);
    }

    public boolean isValid(ExternalEvent event) {
        if (!invalidActions.contains(event)) {
            if (filters != null) {
                for (Predicate<ExternalEvent> filter : filters) {
                    if (!filter.test(event)) {
                        invalidActions.add(event);
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Visible
    public HashSet<ExternalEvent> getInvalidActions() {
        return invalidActions;
    }
}
