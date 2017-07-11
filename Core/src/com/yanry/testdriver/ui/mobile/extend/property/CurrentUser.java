package com.yanry.testdriver.ui.mobile.extend.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.property.SwitchBySearchProperty;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by rongyu.yan on 4/18/2017.
 */
public class CurrentUser extends SwitchBySearchProperty<String> {
    private Graph graph;
    private HashMap<String, String> userPasswordMap;

    public CurrentUser(Graph graph) {
        this.graph = graph;
        userPasswordMap = new HashMap<>();
    }

    public CurrentUser addUserPassword(String user, String pwd) {
        userPasswordMap.put(user, pwd);
        return this;
    }

    public HashMap<String, String> getUserPasswordMap() {
        return userPasswordMap;
    }

    @Override
    protected boolean isVisibleToUser() {
        return false;
    }

    @Override
    protected String checkValue() {
        Set<String> users = userPasswordMap.keySet();
        String[] options = new String[users.size() + 1];
        options[0] = "";
        int i = 0;
        for (String user : users) {
            options[++i] = user;
        }
        return getGraph().checkState(new StateToCheck<>(this, options));
    }

    @Override
    protected Graph getGraph() {
        return graph;
    }
}
