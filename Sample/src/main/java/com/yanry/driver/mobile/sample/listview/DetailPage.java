package com.yanry.driver.mobile.sample.listview;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.Text;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;

public abstract class DetailPage extends Window {
    private Text tvPrincipal;
    private Text tvFinishDate;
    private Text tvTotalRate;

    public DetailPage(WindowManager manager) {
        super(manager);
        tvPrincipal = new Text(getViewById("tv_principal"));
        tvFinishDate = new Text(getViewById("tv_finish_date"));
        tvTotalRate = new Text(getViewById("tv_total_rate"));
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        closeOnPressBack();
        createPath(getCreateEvent(), tvPrincipal.getDynamicExpectation(Timing.IMMEDIATELY, true, () -> getMainPage().getClickItem().getPreActionResult().getTvMoney().getCurrentValue()));
        createPath(getCreateEvent(), tvFinishDate.getDynamicExpectation(Timing.IMMEDIATELY, true, () -> getMainPage().getClickItem().getPreActionResult().getTvFinishDate().getCurrentValue()));
        createPath(getCreateEvent(), tvTotalRate.getDynamicExpectation(Timing.IMMEDIATELY, true, () -> getMainPage().getClickItem().getPreActionResult().getTvTotalRate().getCurrentValue()));

        // 编辑
        popWindow(getEditPage(), new Click(getViewById("tv_edit")), Timing.IMMEDIATELY, false);
        // 删除
        close(new Click(getViewById("tv_delete")), Timing.IMMEDIATELY, getMainPage().getListView().getSize().getShiftExpectation(Timing.IMMEDIATELY, true, false, 1));
    }

    protected abstract MainPage getMainPage();

    protected abstract EditPage getEditPage();
}
