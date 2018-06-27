package com.yanry.testdriver.ui.mobile.extend.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.runtime.StateToCheck;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by rongyu.yan on 4/18/2017.
 */
public class CurrentUser extends CacheProperty<String> {
    private HashMap<String, String> userPasswordMap;

    public CurrentUser() {
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
    protected String checkValue(Graph graph) {
        Set<String> users = userPasswordMap.keySet();
        String[] options = new String[users.size() + 1];
        options[0] = "";
        int i = 0;
        for (String user : users) {
            options[++i] = user;
        }
        return graph.checkState(new StateToCheck<>(this, options));
    }

    @Override
    public boolean isCheckedByUser() {
        return false;
    }

    @Override
    protected boolean doSelfSwitch(Graph graph, String to) {
        return false;
    }
}
