package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.property.QueryableProperty;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.view.TextView;
import com.yanry.testdriver.ui.mobile.extend.view.container.ListView;
import com.yanry.testdriver.ui.mobile.extend.view.container.ListViewItem;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class SelectDateOnMain extends TestManager.Window {
    public SelectDateOnMain(TestManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        ListView lvDate = new ListView(this, null);
        // 随机点击一个item返回主页
        Click<ListViewItem, String[]> clickItem = new Click<>(lvDate.getRandomItem(getGraph()));
        clickItem.setPreAction(item -> {
            TextView dateView = new TextView(item, null);
            dateView.getText().doQuery(getGraph());
            QueryableProperty month = new QueryableProperty("所选月份");
            month.doQuery(getGraph());
            QueryableProperty year = new QueryableProperty("所选年份");
            year.doQuery(getGraph());
            return new String[]{dateView.getText().getValue(true), month.getValue(true), year.getValue(true)};
        });
        // 校验日期标签是否正确显示
        close(clickItem, Timing.IMMEDIATELY).addFollowingAction(superPaths -> {
            TextView tvDate = getWindow(Main.class).getView(Main.TV_DATE);
            tvDate.getText().getDynamicExpectation(getGraph(), Timing.IMMEDIATELY, () -> {
                String[] result = clickItem.getPreActionResult();
                return String.format("%s-%s-%s", result[2], result[1], result[0]);
            }).verify(superPaths);
        });
    }
}
