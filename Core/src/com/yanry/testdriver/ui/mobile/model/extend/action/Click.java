package com.yanry.testdriver.ui.mobile.model.extend.action;

import com.yanry.testdriver.ui.mobile.model.base.ActionEvent;
import com.yanry.testdriver.ui.mobile.model.base.Presentable;
import com.yanry.testdriver.ui.mobile.model.base.view.View;

/**
 * Created by rongyu.yan on 2/18/2017.
 */
@Presentable
public class Click implements ActionEvent {
    private View view;

    public Click(View view) {
        this.view = view;
    }

    @Presentable
    public View getView() {
        return view;
    }
}
