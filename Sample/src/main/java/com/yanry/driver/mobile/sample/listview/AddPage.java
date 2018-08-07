package com.yanry.driver.mobile.sample.listview;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.expectation.Toast;
import com.yanry.driver.mobile.property.Text;
import com.yanry.driver.mobile.property.TextValidity;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;

public class AddPage extends Window {
    public AddPage(WindowManager manager) {
        super(manager);
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        closeOnPressBack();

        Click clickSubmit = new Click(getViewById("tv_done"));
        Toast toast = new Toast(Timing.IMMEDIATELY, graph, 3000, "输入不完整！");
        TextValidity principalValidity = new TextValidity(getViewById("tv_principal"), new Text(getViewById("tv_principal")));
        principalValidity.addNegativeCase("", clickSubmit, toast);
        TextValidity bonusValidity = new TextValidity(getViewById("tv_bonus"), new Text(getViewById("tv_bonus")));
        TextValidity interestValidity = new TextValidity(getViewById("tv_raw_interest"), new Text(getViewById("tv_raw_interest")));
        TextValidity daysValidity = new TextValidity(getViewById("tv_invest_days"), new Text(getViewById("tv_invest_days")));
        TextValidity totalDaysValidity = new TextValidity(getViewById("tv_total_days"), new Text(getViewById("tv_total_days")));

    }
}
