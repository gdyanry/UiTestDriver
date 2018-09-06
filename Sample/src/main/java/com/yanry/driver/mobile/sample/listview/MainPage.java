package com.yanry.driver.mobile.sample.listview;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.state.UnaryIntPredicate;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.Text;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.listview.ListView;
import com.yanry.driver.mobile.view.listview.ListViewItem;
import com.yanry.driver.mobile.view.selector.ById;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;

public class MainPage extends Window {
    private ListView<MainListItem> listView;

    public MainPage(Graph graph, WindowManager manager) {
        super(graph, manager);
        listView = new ListView<>(graph, this, new ById("lv"), (g, l, i) -> new MainListItem(g, l, i));
    }

    public ListView<MainListItem> getListView() {
        return listView;
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        showOnStartUp(Timing.IMMEDIATELY);
        closeOnPressBack();
        // 点击列表项进入详情页
        popWindow(DetailPage.class, listView.getClickItemEvent(), Timing.IMMEDIATELY, false)
                .addInitState(listView.getSize(), 1)
                .addInitStatePredicate(listView.getSize(), new UnaryIntPredicate(0, true));
        // 筛选
        popWindow(FilterPage.class, new Click(new View(graph, this, new ById("filter"))), Timing.IMMEDIATELY, false);
        // 添加
        popWindow(EditPage.class, new Click(new View(graph, this, new ById("add"))), Timing.IMMEDIATELY, false);
    }

    public class MainListItem extends ListViewItem<MainListItem> {
        private Text tvFinishDate;
        private Text tvMoney;
        private Text tvTotalRate;

        public MainListItem(Graph graph, ListView<MainListItem> parent, int index) {
            super(graph, parent, index);
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
        protected void fetchViewPropertyValues() {
            tvFinishDate.getCurrentValue();
            tvMoney.getCurrentValue();
            tvTotalRate.getCurrentValue();
        }
    }
}
