package com.yanry.driver.mobile.sample.listview;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.state.UnaryIntPredicate;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.Text;
import com.yanry.driver.mobile.view.ListView;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ById;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;

public abstract class MainPage extends Window {
    private ListView listView;
    private Click<ItemData> clickItem;

    public MainPage(WindowManager manager) {
        super(manager);
        listView = new ListView(getGraph(), this, new ById("lv"));
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        closeOnPressBack();
        clickItem = new Click<>(listView.getRandomItem());
        clickItem.setPreAction(listViewItem -> new ItemData(listViewItem));
        // 点击列表项进入详情页
        popWindow(getDetailPage(), clickItem, Timing.IMMEDIATELY, false)
                .addInitState(listView.getSize(), 1)
                .addInitStatePredicate(listView.getSize(), new UnaryIntPredicate(0, true));
        // 筛选
        popWindow(getFilterPage(), new Click(new View(graph, this, new ById("tv_filter"))), Timing.IMMEDIATELY, false);
        // 添加
        popWindow(getAddPage(), new Click(new View(graph, this, new ById("tv_add"))), Timing.IMMEDIATELY, false);
    }

    public Click<ItemData> getClickItem() {
        return clickItem;
    }

    public ListView getListView() {
        return listView;
    }

    protected abstract DetailPage getDetailPage();

    protected abstract FilterPage getFilterPage();

    protected abstract EditPage getAddPage();

    public class ItemData {
        private String finishDate;
        private String money;
        private String totalRate;

        private ItemData(View listViewItem) {
            finishDate = new Text(listViewItem.getViewById("tv_finish_date")).getCurrentValue();
            money = new Text(listViewItem.getViewById("tv_money")).getCurrentValue();
            totalRate = new Text(listViewItem.getViewById("tv_bonus_interest_rate")).getCurrentValue();
        }

        public String getFinishDate() {
            return finishDate;
        }

        public String getMoney() {
            return money;
        }

        public String getTotalRate() {
            return totalRate;
        }
    }
}
