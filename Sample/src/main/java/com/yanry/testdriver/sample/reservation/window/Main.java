package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.WindowManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.view.TextView;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByDesc;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class Main extends WindowManager.Window {
    private TextView tvDate;

    public Main(WindowManager manager) {
        manager.super();
        tvDate = new TextView(getManager(), this, new ByDesc("日期标签"));
    }

    public TextView getTvDate() {
        return tvDate;
    }

    @Override
    protected void addCases() {
        showOnStartUp(Timing.IMMEDIATELY);
        SelectDateOnMain selectDateOnMain = new SelectDateOnMain(getManager());
        popWindow(selectDateOnMain, new Click<>(new View(getManager(), this, new ByDesc("选择日期图标"))), Timing
                .IMMEDIATELY, false, true);
        popWindow(selectDateOnMain, new Click<>(tvDate), Timing.IMMEDIATELY, false, true);
        popWindow(selectDateOnMain, new Click(new View(getManager(), this, new ByText("筛选"))), Timing.IMMEDIATELY,
                false, false);
        popWindow(new SpecificationOnMain(getManager()), new Click(new View(getManager(), this, new ByDesc("问号图标"))), Timing
                .IMMEDIATELY, false, false);
        popWindow(new MenuOnMain(getManager()), new Click(new View(getManager(), this, new ByDesc("菜单图标"))), Timing.IMMEDIATELY,
                false, false);
    }
}
