package com.yanry.driver.mobile.sample.listview;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.predicate.Equals;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.EditText;
import com.yanry.driver.mobile.property.TextValidity;
import com.yanry.driver.mobile.sample.listview.MainPage.MainListItem;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.listview.ListView;
import com.yanry.driver.mobile.view.selector.ById;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;

public class EditPage extends Window {
    public EditPage(Graph graph, WindowManager manager) {
        super(graph, manager);
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        closeOnPressBack();
        TextValidity etPrincipal = getTextValidity("et_principal");
        etPrincipal.addPositiveCases("40000");
        etPrincipal.addNegativeCase("");
        TextValidity etBonus = getTextValidity("et_bonus");
        etBonus.addNegativeCase("");
        etBonus.addPositiveCases("880");
        TextValidity etRawInterestRate = getTextValidity("et_raw_interest_rate");
        etRawInterestRate.addPositiveCases("12");
        etRawInterestRate.addNegativeCase("");
        TextValidity etPassDays = getTextValidity("et_pass_days");
        etPassDays.addNegativeCase("");
        etPassDays.addPositiveCases("5");
        TextValidity etTotalDays = getTextValidity("et_total_days");
        etTotalDays.addPositiveCases("90");
        etTotalDays.addNegativeCase("");
//        EditText etRemark = new EditText(getViewById("et_remark"));
        Click click = new Click(new View(graph, this, new ById("finish")));
        // 添加
        ListView<MainListItem> listView = getWindow(MainPage.class).getListView();
        createForegroundPath(click, listView.getSize().getShiftExpectation(Timing.IMMEDIATELY, true, true, 1).setTrigger(listView, new Equals<>(true)))
                .addContextState(getPreviousWindow(), getWindow(MainPage.class))
                .addContextState(etPrincipal, true)
                .addContextState(etBonus, true)
                .addContextState(etRawInterestRate, true)
                .addContextState(etPassDays, true)
                .addContextState(etTotalDays, true);
        close(click, Timing.IMMEDIATELY).addContextState(etPrincipal, true)
                .addContextState(etBonus, true)
                .addContextState(etRawInterestRate, true)
                .addContextState(etPassDays, true)
                .addContextState(etTotalDays, true);
        ;
    }

    private TextValidity getTextValidity(String viewId) {
        return new TextValidity(getViewById(viewId), new EditText(getViewById(viewId)));
    }
}
