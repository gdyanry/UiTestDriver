package com.yanry.driver.mobile.action;

import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.mobile.view.View;

import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 2/18/2017.
 */
@Presentable
public class Click<V extends View, R> extends ActionEvent<V, R> {

    public Click(Supplier<V> dataSupplier) {
        super(dataSupplier);
    }

    public Click(V data) {
        super(data);
    }
}
