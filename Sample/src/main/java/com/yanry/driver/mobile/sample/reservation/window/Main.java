package com.yanry.driver.mobile.sample.reservation.window;

import com.yanry.driver.core.extend.WindowManager;
import com.yanry.driver.core.extend.action.Click;
import com.yanry.driver.core.extend.view.TextView;
import com.yanry.driver.core.extend.view.View;
import com.yanry.driver.core.extend.view.selector.ByDesc;
import com.yanry.driver.core.extend.view.selector.ByText;
import com.yanry.driver.core.model.expectation.Timing;

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
