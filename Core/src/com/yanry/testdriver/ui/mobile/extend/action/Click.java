package com.yanry.testdriver.ui.mobile.extend.action;

import com.yanry.testdriver.ui.mobile.base.event.ActionEvent;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.extend.view.View;

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
