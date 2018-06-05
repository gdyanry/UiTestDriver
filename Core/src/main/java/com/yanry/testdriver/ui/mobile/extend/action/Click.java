package com.yanry.testdriver.ui.mobile.extend.action;

import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.event.ActionEvent;
import com.yanry.testdriver.ui.mobile.extend.view.View;

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
