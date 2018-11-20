package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.Divider;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.core.model.runtime.fetch.Select;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by rongyu.yan on 4/18/2017.
 */
public class CurrentUser extends Property<String> {
    private HashMap<String, String> userPasswordMap;
    private Divider<String> loginState;

    public CurrentUser(Graph graph) {
        super(graph);
        userPasswordMap = new HashMap<>();
        loginState = new Divider<>(this, Equals.of("").not());
    }

    public CurrentUser addUserPassword(String user, String pwd) {
        userPasswordMap.put(user, pwd);
        return this;
    }

    public Divider<String> getLoginState() {
        return loginState;
    }

    public HashMap<String, String> getUserPasswordMap() {
        return userPasswordMap;
    }

    @Override
    protected String checkValue() {
        return getGraph().obtainValue(new Select<>(this));
    }

    @Override
    protected ExternalEvent doSelfSwitch(String to) {
        return null;
    }

    @Override
    protected Stream<String> getValueStream(Set<String> collectedValues) {
        return collectedValues.stream();
    }
}
