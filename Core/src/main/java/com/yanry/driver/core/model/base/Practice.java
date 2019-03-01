package com.yanry.driver.core.model.base;

import lib.common.util.object.Visible;

public class Practice extends ActionFilter {
    private ExternalEvent selectedEvent;

    @Visible
    public ExternalEvent getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(ExternalEvent selectedEvent) {
        this.selectedEvent = selectedEvent;
    }
}
