package com.yanry.driver.core.model.base;

import yanry.lib.java.util.object.Visible;
import yanry.lib.java.util.object.VisibleObject;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Predicate;

public class ActionGuard extends VisibleObject {
    private HashSet<ExternalEvent> invalidActions;
    private LinkedList<Predicate<ExternalEvent>> filters;
    private ExternalEvent selectedAction;

    public ActionGuard() {
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

    public void setSelectedAction(ExternalEvent selectedAction) {
        this.selectedAction = selectedAction;
    }

    @Visible
    public ExternalEvent getSelectedAction() {
        return selectedAction;
    }

    @Visible
    public HashSet<ExternalEvent> getInvalidActions() {
        return invalidActions;
    }
}
