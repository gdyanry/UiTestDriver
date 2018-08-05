package com.yanry.driver.mobile.sample.interest.window;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.mobile.WindowManager;
import com.yanry.driver.mobile.WindowManager.Window;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.Text;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.container.ListView;
import com.yanry.driver.mobile.view.container.ListViewItem;
import com.yanry.driver.mobile.view.selector.ById;

public class Main extends Window {
    private ListView listView;

    public Main(WindowManager manager) {
        manager.super();
        listView = new ListView(getGraph(), this, new ById("lv"));
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        Click<ListViewItem, ItemData> clickItem = new Click<>(listView.getRandomItem());
        clickItem.setPreAction(listViewItem -> {
            ItemData itemData = new ItemData(graph, listViewItem);
            itemData.tvFinishDate.getCurrentValue();
            return itemData;
        });
    }

    public class ItemData {
        private Text tvFinishDate;
        private Text tvMoney;
        private Text tvTotalRate;

        private ItemData(Graph graph, ListViewItem listViewItem) {
            tvFinishDate = new Text(new View(graph, listViewItem, new ById("tv_finishDate")));
            tvMoney = new Text(new View(graph, listViewItem, new ById("tv_money")));
            tvTotalRate = new Text(new View(graph, listViewItem, new ById("tv_bonus_interest_rate")));
        }
    }

}
