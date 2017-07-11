package com.yanry.testdriver.ui.mobile.base.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;

import java.util.List;

/**
 * Created by Administrator on 2017/7/11.
 */
public abstract class QueryProperty extends CacheSwitchableProperty<String> {

    protected abstract Graph getGraph();

    @Presentable
    public abstract Object getIdentifier();

    @Override
    protected String checkValue() {
        return getGraph().queryValue(this);
    }

    @Override
    protected boolean doSwitch(String to, List<Path> parentPaths) {
        return false;
    }
}
