package com.yanry.driver.mobile.sample.reservation.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.Text;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByDesc;
import com.yanry.driver.mobile.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class Main extends Window {
    private Text txtDate;

    public Main(WindowManager manager) {
        super(manager);
        View tvDate = new View(getGraph(), this, new ByDesc("日期标签"));
        txtDate = new Text(tvDate);
    }

    public Text getTxtDate() {
        return txtDate;
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        showOnStartUp(Timing.IMMEDIATELY);
        SelectDateOnMain selectDateOnMain = new SelectDateOnMain(getManager());
        popWindow(selectDateOnMain, new Click<>(new View(graph, this, new ByDesc("选择日期图标"))), Timing
                .IMMEDIATELY, false);
        popWindow(selectDateOnMain, new Click<>(txtDate.getView()), Timing.IMMEDIATELY, false);
        popWindow(selectDateOnMain, new Click(new View(graph, this, new ByText("筛选"))), Timing.IMMEDIATELY,
                false);
        popWindow(new SpecificationOnMain(getManager()), new Click(new View(graph, this, new ByDesc("问号图标"))), Timing
                .IMMEDIATELY, false);
        popWindow(new MenuOnMain(getManager()), new Click(new View(graph, this, new ByDesc("菜单图标"))), Timing.IMMEDIATELY,
                false);
    }
}
