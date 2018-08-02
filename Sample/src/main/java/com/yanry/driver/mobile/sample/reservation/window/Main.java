package com.yanry.driver.mobile.sample.reservation.window;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.WindowManager;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.view.TextView;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByDesc;
import com.yanry.driver.mobile.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class Main extends WindowManager.Window {
    private TextView tvDate;

    public Main(WindowManager manager) {
        manager.super();
    }

    public TextView getTvDate() {
        return tvDate;
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        TextView tvDate = new TextView(graph, this, new ByDesc("日期标签"));
        showOnStartUp(Timing.IMMEDIATELY);
        SelectDateOnMain selectDateOnMain = new SelectDateOnMain(getManager());
        popWindow(selectDateOnMain, new Click<>(new View(graph, this, new ByDesc("选择日期图标"))), Timing
                .IMMEDIATELY, false, true);
        popWindow(selectDateOnMain, new Click<>(tvDate), Timing.IMMEDIATELY, false, true);
        popWindow(selectDateOnMain, new Click(new View(graph, this, new ByText("筛选"))), Timing.IMMEDIATELY,
                false, false);
        popWindow(new SpecificationOnMain(getManager()), new Click(new View(graph, this, new ByDesc("问号图标"))), Timing
                .IMMEDIATELY, false, false);
        popWindow(new MenuOnMain(getManager()), new Click(new View(graph, this, new ByDesc("菜单图标"))), Timing.IMMEDIATELY,
                false, false);
    }
}
