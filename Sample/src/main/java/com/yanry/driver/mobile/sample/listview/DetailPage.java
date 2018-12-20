package com.yanry.driver.mobile.sample.listview;

import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.Text;
import com.yanry.driver.mobile.view.listview.ListView;
import com.yanry.driver.mobile.window.Application;
import com.yanry.driver.mobile.window.Window;

public class DetailPage extends Window {
    private Text tvPrincipal;
    private Text tvFinishDate;
    private Text tvTotalRate;

    public DetailPage(StateSpace stateSpace, Application manager) {
        super(stateSpace, manager);
        tvPrincipal = new Text(getViewById("tv_principal"));
        tvFinishDate = new Text(getViewById("tv_finish_date"));
        tvTotalRate = new Text(getViewById("tv_total_rate"));
    }

    @Override
    protected void addCases(StateSpace stateSpace, Application manager) {
        closeOnPressBack();
        ListView<MainPage.MainListItem> listView = getWindow(MainPage.class).getListView();
        stateSpace.createPath(getCreateEvent(), tvPrincipal.getDynamicExpectation(Timing.IMMEDIATELY, true,
                () -> listView.getValueFromClickedItem(item -> item.getTvMoney().getCurrentValue())));
        stateSpace.createPath(getCreateEvent(), tvFinishDate.getDynamicExpectation(Timing.IMMEDIATELY, true,
                () -> listView.getValueFromClickedItem(item -> item.getTvFinishDate().getCurrentValue())));
        stateSpace.createPath(getCreateEvent(), tvTotalRate.getDynamicExpectation(Timing.IMMEDIATELY, true,
                () -> listView.getValueFromClickedItem(item -> item.getTvTotalRate().getCurrentValue())));
        // 编辑
        Click clickEdit = new Click(getViewById("edit"));
        popWindow(EditPage.class, clickEdit, Timing.IMMEDIATELY, false);
        // 删除
        Click clickDel = new Click(getViewById("delete"));
        close(clickDel, Timing.IMMEDIATELY).getExpectation()
                .addFollowingExpectation(listView.getSize().getShiftExpectation(Timing.IMMEDIATELY, true, false, 1));
    }
}
