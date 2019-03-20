package com.yanry.driver.core.model.runtime.record;

import com.yanry.driver.core.model.base.ExternalEvent;
import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.HandyObject;
import yanry.lib.java.util.object.Visible;

public class ActionRecord extends HandyObject implements CommunicateRecord {
    private ExternalEvent action;
    private boolean performed;
    private String graphSnapShoot;

    public ActionRecord(ExternalEvent action, boolean performed, String graphSnapShoot) {
        this.action = action;
        this.performed = performed;
        this.graphSnapShoot = graphSnapShoot;
    }

    @EqualsPart
    @Visible
    public ExternalEvent getAction() {
        return action;
    }

    @Visible
    public boolean isPerformed() {
        return performed;
    }

    @EqualsPart
    public String getGraphSnapShoot() {
        return graphSnapShoot;
    }
}
