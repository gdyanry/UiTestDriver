package com.yanry.driver.mobile.sample.interest.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.Text;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.container.ListView;
import com.yanry.driver.mobile.view.container.ListViewItem;
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
        // 点击进入详情页
        popWindow(getDetailPage(), clickItem, Timing.IMMEDIATELY, false).addInitState(listView.getSize(), 1);
    }

    public Click<ListViewItem, ItemData> getClickItem() {
        return clickItem;
    }

    protected abstract DetailPage getDetailPage();

    public class ItemData {
        private Text tvFinishDate;
        private Text tvMoney;
        private Text tvTotalRate;

        private ItemData(Graph graph, ListViewItem listViewItem) {
            tvFinishDate = new Text(new View(graph, listViewItem, new ById("tv_finishDate")));
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
