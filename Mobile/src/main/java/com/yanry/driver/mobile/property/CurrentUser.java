package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.fetch.Select;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by rongyu.yan on 4/18/2017.
 */
public class CurrentUser extends Property<String> {
    private HashMap<String, String> userPasswordMap;

    public CurrentUser(Graph graph) {
        super(graph);
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
    protected String fetchValue() {
        Set<String> users = userPasswordMap.keySet();
        String[] options = new String[users.size() + 1];
        options[0] = "";
        int i = 0;
        for (String user : users) {
            options[++i] = user;
        }
        return getGraph().obtainValue(new Select<>(this, options));
    }

    @Override
    protected SwitchResult doSelfSwitch(String to) {
        return SwitchResult.NoAction;
    }
}
