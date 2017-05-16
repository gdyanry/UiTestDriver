package com.yanry.testdriver.ui.mobile.extend.property;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.property.CacheSwitchableProperty;
import com.yanry.testdriver.ui.mobile.base.property.SearchableSwitchableProperty;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;
import com.yanry.testdriver.ui.mobile.base.Graph;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 4/18/2017.
 */
public class CurrentUser extends SearchableSwitchableProperty<String> {
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
    protected boolean ifNeedVerification() {
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
