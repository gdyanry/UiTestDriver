package com.yanry.driver.mobile.sample.listview;

import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.predicate.GreaterThan;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.Text;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.listview.ListView;
import com.yanry.driver.mobile.view.listview.ListViewItem;
import com.yanry.driver.mobile.view.selector.ById;
import com.yanry.driver.mobile.window.Application;
import com.yanry.driver.mobile.window.Window;

public class MainPage extends Window {
    private ListView<MainListItem> listView;

    public MainPage(StateSpace stateSpace, Application manager) {
        super(stateSpace, manager);
        listView = new ListView<>(stateSpace, this, new ById("lv"), (g, l, i) -> new MainListItem(g, l, i));
    }

    public ListView<MainListItem> getListView() {
        return listView;
    }

    @Override
    protected void addCases(StateSpace stateSpace, Application manager) {
        showOnLaunch(Timing.IMMEDIATELY);
        closeOnPressBack();
        // 点击列表项进入详情页
        popWindow(DetailPage.class, listView.getClickItemEvent(), Timing.IMMEDIATELY, false)
                .addContextPredicate(listView.getSize(), new GreaterThan<>(0));
        // 筛选
        popWindow(FilterPage.class, new Click(new View(stateSpace, this, new ById("filter"))), Timing.IMMEDIATELY, false);
        // 添加
        popWindow(EditPage.class, new Click(new View(stateSpace, this, new ById("add"))), Timing.IMMEDIATELY, false);
    }

    public class MainListItem extends ListViewItem<MainListItem> {
        private Text tvFinishDate;
        private Text tvMoney;
        private Text tvTotalRate;

        public MainListItem(StateSpace stateSpace, ListView<MainListItem> parent, int index) {
            super(stateSpace, parent, index);
            tvFinishDate = new Text(getViewById("tv_finish_date"));
            tvMoney = new Text(getViewById("tv_money"));
            tvTotalRate = new Text(getViewById("tv_bonus_interest_rate"));
        }

        public Text getTvFinishDate() {
            return tvFinishDate;
        }

        public Text getTvMoney() {
            return tvMoney;
        }

        public Text getTvTotalRate() {
            return tvTotalRate;
        }

        @Override
        protected void queryViewStates() {
            tvFinishDate.getCurrentValue();
            tvMoney.getCurrentValue();
            tvTotalRate.getCurrentValue();
        }
    }
}
