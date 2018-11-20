package com.yanry.driver.mobile.sample.listview;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.ValuePredicate;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.EditText;
import com.yanry.driver.mobile.sample.listview.MainPage.MainListItem;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.listview.ListView;
import com.yanry.driver.mobile.view.selector.ById;
import com.yanry.driver.mobile.window.Application;
import com.yanry.driver.mobile.window.Window;

public class EditPage extends Window {
    public EditPage(Graph graph, Application manager) {
        super(graph, manager);
    }

    @Override
    protected void addCases(Graph graph, Application manager) {
        ValuePredicate<String> notEmpty = Equals.of("").not();
        closeOnPressBack();
        EditText et_principal = new EditText(getViewById("et_principal"));
        et_principal.addValue("40000", "28800");
        EditText etBonus = new EditText(getViewById("et_bonus"));
        etBonus.addValue("880", "300");
        EditText etRawInterestRate = new EditText(getViewById("et_raw_interest_rate"));
        etRawInterestRate.addValue("12", "8");
        EditText etPassDays = new EditText(getViewById("et_pass_days"));
        etPassDays.addValue("5", "19");
        EditText etTotalDays = new EditText(getViewById("et_total_days"));
        etTotalDays.addValue("90", "60");
//        EditText etRemark = new EditText(getViewById("et_remark"));
        Click clickFinish = new Click(new View(graph, this, new ById("finish")));
        // 添加
        ListView<MainListItem> listView = getWindow(MainPage.class).getListView();
        graph.createPath(clickFinish, listView.getSize().getShiftExpectation(Timing.IMMEDIATELY, true, true, 1)
                .setTrigger(listView, Equals.of(true)))
                .addContextValue(getPreviousWindow(), getWindow(MainPage.class))
                .addContextPredicate(et_principal, notEmpty)
                .addContextPredicate(etBonus, notEmpty)
                .addContextPredicate(etRawInterestRate, notEmpty)
                .addContextPredicate(etPassDays, notEmpty)
                .addContextPredicate(etTotalDays, notEmpty)
                .addContextValue(this, true);
        close(clickFinish, Timing.IMMEDIATELY)
                .addContextPredicate(et_principal, notEmpty)
                .addContextPredicate(etBonus, notEmpty)
                .addContextPredicate(etRawInterestRate, notEmpty)
                .addContextPredicate(etPassDays, notEmpty)
                .addContextPredicate(etTotalDays, notEmpty)
                .addContextValue(this, true);
    }
}
