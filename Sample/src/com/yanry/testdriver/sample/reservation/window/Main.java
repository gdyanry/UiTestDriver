package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.view.TextView;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByDesc;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class Main extends TestManager.Window {
    private TextView tvDate;

    public Main(TestManager manager) {
        manager.super();
        tvDate = new TextView(this, new ByDesc("日期标签"));
    }

    public TextView getTvDate() {
        return tvDate;
    }

    @Override
    protected void addCases() {
        showOnStartUp(Timing.IMMEDIATELY);
        popWindow(getWindow(SelectDateOnMain.class), new Click<>(new View(this, new ByDesc("选择日期图标"))), Timing
                .IMMEDIATELY, false, true);
        popWindow(getWindow(SelectDateOnMain.class), new Click<>(tvDate), Timing.IMMEDIATELY, false, true);
        popWindow(getWindow(FilterOnMain.class), new Click(new View(this, new ByText("筛选"))), Timing.IMMEDIATELY,
                false, false);
        popWindow(getWindow(SpecificationOnMain.class), new Click(new View(this, new ByDesc("问号图标"))), Timing
                .IMMEDIATELY, false, false);
        popWindow(getWindow(MenuOnMain.class), new Click(new View(this, new ByDesc("菜单图标"))), Timing.IMMEDIATELY,
                false, false);
    }
}
