package com.yanry.driver.mobile.sample.listview;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.state.UnaryIntPredicate;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.Text;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.ListView;
import com.yanry.driver.mobile.view.ListViewItem;
import com.yanry.driver.mobile.view.selector.ById;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;

public abstract class MainPage extends Window {
    private ListView listView;
    private Click<ListViewItem, ItemData> clickItem;

    public MainPage(WindowManager manager) {
        super(manager);
        listView = new ListView(getGraph(), this, new ById("lv"));
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        closeOnPressBack();
        clickItem = new Click<>(listView.getRandomItem());
        clickItem.setPreAction(listViewItem -> {
            ItemData itemData = new ItemData(graph, listViewItem);
            return itemData;
        });
        // 点击列表项进入详情页
        popWindow(getDetailPage(), clickItem, Timing.IMMEDIATELY, false).addInitState(listView.getSize(), 1)
                .addStatePredicate(listView.getSize(), new UnaryIntPredicate(0, true));
        // 筛选
        popWindow(getFilterPage(), new Click(new View(graph, this, new ById("tv_filter"))), Timing.IMMEDIATELY, false);
        // 添加
        popWindow(getAddPage(), new Click(new View(graph, this, new ById("tv_add"))), Timing.IMMEDIATELY, false);

    }

    public Click<ListViewItem, ItemData> getClickItem() {
        return clickItem;
    }

    public ListView getListView() {
        return listView;
    }

    protected abstract DetailPage getDetailPage();

    protected abstract FilterPage getFilterPage();

    protected abstract AddPage getAddPage();

    public class ItemData {
        private Text tvFinishDate;
        private Text tvMoney;
        private Text tvTotalRate;

        private ItemData(Graph graph, ListViewItem listViewItem) {
            tvFinishDate = new Text(new View(graph, listViewItem, new ById("tv_finish_date")));
            tvMoney = new Text(new View(graph, listViewItem, new ById("tv_money")));
            tvTotalRate = new Text(new View(graph, listViewItem, new ById("tv_bonus_interest_rate")));
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
    }
}
